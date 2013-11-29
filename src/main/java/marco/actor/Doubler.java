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
		this.nodes = nodes;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (log.isDebugEnabled()) log.debug("{} {} received {} from {}", actorName, getSelf(), message, getSender());
		if (message instanceof Integer) {
			Integer input = (Integer) message;
			ActorRef requester = getSender();
			ActorRef first = buildIncrementers(input, requester);
			first.tell(input, getSelf());
			if (log.isDebugEnabled()) log.debug("{} sent {} to {}", getSelf(), input, first);
		} else unhandled(message);

	}

	private ActorRef buildIncrementers(Integer input, ActorRef requester) {
		ActorRef next = requester;
		for (int i = input; i > 0; i--) {
			int nodeIndex = (i - 1) % nodes.size();
			ActorRef incrementer = getContext().actorOf(
					Incrementer.mkProps(next).withDeploy(new Deploy(new RemoteScope(nodes.get(nodeIndex)))),
					Incrementer.actorName + "-" + i);
			next = incrementer;
		}
		return next;
	}

}
