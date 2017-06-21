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
	PLACE_SPAWNPOINT {
		@Override
		public Action getActionInstance() {
			return new PlaceSpawnpointAction();
		}
	},
	AUTO_PLACE {
		@Override
		public Action getActionInstance() {
			return new AutoPlaceAction();
		}
	};
	
	public abstract Action getActionInstance();
	
	
}
