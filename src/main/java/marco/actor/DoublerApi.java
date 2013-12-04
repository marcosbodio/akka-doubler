package marco.actor;

import java.io.Serializable;

public interface DoublerApi {

	public static final class Computation implements Serializable {

		private static final long serialVersionUID = 5726312918035583340L;
		private final int value;
		private final int steps;

		public Computation(int value, int steps) {
			this.value = value;
			this.steps = steps;
		}

		public int getValue() {
			return value;
		}

		public int getSteps() {
			return steps;
		}

		@Override
		public String toString() {
			return "Computation [value=" + value + ", steps=" + steps + "]";
		}

	}

}
