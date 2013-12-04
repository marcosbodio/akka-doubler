package marco.actor;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.RemoteScope;

public class Incrementer extends UntypedActor {

	public static final String actorName = "incrementer";

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final ActorRef target;

	private final List<Address> nodes;

	private final int nextNodeIndex;

	public static Props mkProps(ActorRef target, List<Address> nodes, int nextNodeIndex) {
		return Props.create(Incrementer.class, target, nodes, nextNodeIndex);
	}

	public Incrementer(ActorRef target, List<Address> nodes, int nextNodeIndex) {
		this.target = target;
		this.nodes = nodes;
		this.nextNodeIndex = nextNodeIndex;
		if (log.isDebugEnabled())
			log.debug("{} created with target = {} and next node = {}", getSelf(), target, nodes.get(nextNodeIndex));
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (log.isDebugEnabled()) log.debug("{} {} received {} from {}", actorName, getSelf(), message, getSender());
		if (message instanceof DoublerApi.Computation) {
			DoublerApi.Computation computation = (DoublerApi.Computation) message;
			if (computation.getSteps() == 0) {
				target.tell(computation.getValue(), getSelf());
				if (log.isDebugEnabled())
					log.debug("computation complete: {} sent {} to {}", getSelf(), computation.getValue(), target);
			} else {
				DoublerApi.Computation nextComputation = new DoublerApi.Computation(computation.getValue() + 1,
						computation.getSteps() - 1);
				ActorRef next = getContext().actorOf(
						Incrementer.mkProps(target, nodes, (nextNodeIndex + 1) % nodes.size()).withDeploy(
								new Deploy(new RemoteScope(nodes.get(nextNodeIndex)))),
						Incrementer.actorName + "-" + nextComputation.getSteps());
				next.tell(nextComputation, getSelf());
				if (log.isDebugEnabled()) log.debug("{} sent {} to {}", getSelf(), nextComputation, next);
			}
		} else unhandled(message);
	}

}
