package com.fullspectrum.fsm.transition;

public enum Transition {

	FALLING{
		@Override
		public TransitionSystem getSystem() {
			return FallingTransition.getInstance();
		}
	},
	RANDOM{
		@Override
		public TransitionSystem getSystem() {
			return RandomTransition.getInstance();
		}
	},	
	ANIMATION_FINISHED {
		@Override
		public TransitionSystem getSystem() {
			return AnimationFinishedTransition.getInstance();
		}
	},
	LANDED{
		@Override
		public TransitionSystem getSystem() {
			return LandedTransition.getInstance();
		}
	};	
	public abstract TransitionSystem getSystem();
	public String toString(){
		return name();
	}
	
}
