package com.fullspectrum.fsm.transition;

public enum Transition {

	FALLING(false) {
		@Override
		public TransitionSystem getSystem() {
			return FallingTransition.getInstance();
		}
	},
	RANDOM(true) {
		@Override
		public TransitionSystem getSystem() {
			return RandomTransition.getInstance();
		}
	},
	ANIMATION_FINISHED(false) {
		@Override
		public TransitionSystem getSystem() {
			return AnimationFinishedTransition.getInstance();
		}
	},
	LANDED(false) {
		@Override
		public TransitionSystem getSystem() {
			return LandedTransition.getInstance();
		}
	},
	INPUT(true) {
		@Override
		public TransitionSystem getSystem() {
			return InputTransition.getInstance();
		}
	};

	public final boolean allowMultiple;

	private Transition(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	public abstract TransitionSystem getSystem();

	public String toString() {
		return name();
	}

}