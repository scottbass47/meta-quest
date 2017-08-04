package com.fullspectrum.editor.action;

public enum EditorActions {

	ERASE {
		@Override
		public Action getActionInstance() {
			return new EraseAction();
		}
	},
	SELECT {
		public Action getActionInstance() {
			return new SelectAction();
		}
	},
	PLACE {
		@Override
		public Action getActionInstance() {
			return new PlaceAction();
		}
	},
	SELECT_ENEMY {
		@Override
		public Action getActionInstance() {
			return new SelectEnemyAction();
		}
	},
	LEVEL_TRIGGER {
		@Override
		public Action getActionInstance() {
			return null;
		}
	},
	MOVE {
		@Override
		public Action getActionInstance() {
			return new MoveAction();
		}
	};
	
	public abstract Action getActionInstance();
	
	
}
