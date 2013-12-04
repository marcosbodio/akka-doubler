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

public class Doubler extends UntypedActor {

	public static final String actorName = "doubler";

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final List<Address> nodes;

	public static Props mkProps(List<Address> nodes) {
		return Props.create(Doubler.class, nodes);
	}

	public Doubler(List<Address> nodes) {
		if (nodes.size() < 1) throw new IllegalStateException("I need a list of at least one node!");
		this.nodes = nodes;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (log.isDebugEnabled()) log.debug("{} {} received {} from {}", actorName, getSelf(), message, getSender());
		if (message instanceof Integer) {
			Integer input = (Integer) message;
			DoublerApi.Computation nextComputation = new DoublerApi.Computation(input + 1, input - 1);
			ActorRef target = getSender();
			int nextNodeIndex = 0;
			ActorRef next = getContext().actorOf(
					Incrementer.mkProps(target, nodes, (nextNodeIndex + 1) % nodes.size()).withDeploy(
							new Deploy(new RemoteScope(nodes.get(nextNodeIndex)))),
					Incrementer.actorName + "-" + nextComputation.getSteps());
			next.tell(nextComputation, getSelf());
			if (log.isDebugEnabled()) log.debug("{} sent {} to {}", getSelf(), nextComputation, next);
		} else unhandled(message);

	}

}
