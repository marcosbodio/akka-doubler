package marco.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Incrementer extends UntypedActor {

	public static final String actorName = "incrementer";

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final ActorRef next;

	public static Props mkProps(ActorRef next) {
		return Props.create(Incrementer.class, next);
	}

	public Incrementer(ActorRef next) {
		this.next = next;
		if (log.isDebugEnabled()) log.debug("{} created with next = {}", getSelf(), next);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (log.isDebugEnabled()) log.debug("{} {} received {} from {}", actorName, getSelf(), message, getSender());
		if (message instanceof Integer) {
			Integer curr = (Integer) message;
			Integer succ = curr + 1;
			next.tell(succ, getSelf());
			if (log.isDebugEnabled()) log.debug("{} sent {} to {}", getSelf(), succ, next);
		} else unhandled(message);
	}

}
