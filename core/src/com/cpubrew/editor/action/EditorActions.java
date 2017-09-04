package com.cpubrew.editor.action;

public enum EditorActions {

	ERASE {
		@Override
		public EditorAction getActionInstance() {
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
		public EditorAction getActionInstance() {
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
		public EditorAction getActionInstance() {
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
		public EditorAction getActionInstance() {
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
		public EditorAction getActionInstance() {
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
		public EditorAction getActionInstance() {
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
		public EditorAction getActionInstance() {
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
		public EditorAction getActionInstance() {
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
	},
	NEW_LEVEL {
		@Override
		public EditorAction getActionInstance() {
			return new NewLevelAction();
		}

		@Override
		public String getShortcut() {
			return "Ctrl + N";
		}

		@Override
		public String getDisplayName() {
			return "New Level";
		}
	},
	OPEN_LEVEL {
		@Override
		public EditorAction getActionInstance() {
			return new OpenLevelAction();
		}

		@Override
		public String getShortcut() {
			return "Ctrl + O";
		}

		@Override
		public String getDisplayName() {
			return "Open Level";
		}
	};
	
	public abstract EditorAction getActionInstance();
	public abstract String getShortcut();
	public abstract String getDisplayName();
	
	
}
