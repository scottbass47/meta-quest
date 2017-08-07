package com.fullspectrum.editor.action;

public enum EditorActions {

	ERASE {
		@Override
		public Action getActionInstance() {
			return new EraseAction();
		}

		@Override
		public String getShortcut() {
			return "Ctrl + E";
		}

		@Override
		public String getDisplayName() {
			return "Erase";
		}
	},
	SELECT {
		public Action getActionInstance() {
			return new SelectAction();
		}

		@Override
		public String getShortcut() {
			return "Esc";
		}

		@Override
		public String getDisplayName() {
			return "Select";
		}
	},
	PLACE {
		@Override
		public Action getActionInstance() {
			return new PlaceAction();
		}

		@Override
		public String getShortcut() {
			return null;
		}

		@Override
		public String getDisplayName() {
			return "Place";
		}
	},
	FILL {
		@Override
		public Action getActionInstance() {
			return new FillAction();
		}

		@Override
		public String getShortcut() {
			return "Ctrl + F";
		}

		@Override
		public String getDisplayName() {
			return "Fill";
		}
	},
	SELECT_ENEMY {
		@Override
		public Action getActionInstance() {
			return new SelectEnemyAction();
		}

		@Override
		public String getShortcut() {
			return "Ctrl + Q";
		}

		@Override
		public String getDisplayName() {
			return "Select Enemy";
		}
	},
	LEVEL_TRIGGER {
		@Override
		public Action getActionInstance() {
			return null;
		}

		@Override
		public String getShortcut() {
			return null;
		}

		@Override
		public String getDisplayName() {
			return null;
		}
	},
	MOVE {
		@Override
		public Action getActionInstance() {
			return new MoveAction();
		}

		@Override
		public String getShortcut() {
			return null;
		}

		@Override
		public String getDisplayName() {
			return "Move";
		}
	},
	HELP {

		@Override
		public Action getActionInstance() {
			return new HelpAction();
		}

		@Override
		public String getShortcut() {
			return "Ctrl + H";
		}

		@Override
		public String getDisplayName() {
			return "Help";
		}
	};
	
	public abstract Action getActionInstance();
	public abstract String getShortcut();
	public abstract String getDisplayName();
	
	
}
