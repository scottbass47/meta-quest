package com.cpubrew.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.graphics.Color;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.editor.action.ActionManager;
import com.cpubrew.editor.action.EditorActions;
import com.cpubrew.gui.ActionEvent;
import com.cpubrew.gui.ActionListener;
import com.cpubrew.gui.Button;
import com.cpubrew.gui.Container;
import com.cpubrew.gui.Label;
import com.cpubrew.gui.TextField;
import com.cpubrew.level.Level;
import com.cpubrew.level.LevelUtils;

public class NewLevelPanel extends Container {

	private Label nameLabel;
	private Label titleLabel;
	private TextField nameField;
	private Button createButton;
	private Button cancelButton;
	private int borderPadding = 10;
	private int buttonWidth = 100;
	private int buttonHeight = 40;
	private Label invalidNameLabel;
	
	public NewLevelPanel(final ActionManager manager) {
		setSize(300, 150);
		
		AssetLoader loader = AssetLoader.getInstance();
		
		titleLabel = new Label("New Level");
		titleLabel.setFont(loader.getFont(AssetLoader.font24));
		titleLabel.autoSetSize();
		titleLabel.setPosition(width / 2 - titleLabel.getWidth() / 2, height - titleLabel.getHeight() - borderPadding);
		
		nameLabel = new Label("Name: ");
		nameLabel.setFont(loader.getFont(AssetLoader.font18));
		nameLabel.autoSetSize();
		nameLabel.setPosition(borderPadding, titleLabel.getY() - nameLabel.getHeight() - 2 * borderPadding);
		
		nameField = new TextField();
		nameField.setBackgroundColor(Color.BLACK);
		nameField.setSize(width - 2 * borderPadding - (nameLabel.getX() + nameLabel.getWidth()), 30);
		nameField.setPosition(nameLabel.getX() + nameLabel.getWidth() + 5, nameLabel.getY() + nameLabel.getHeight() / 2 - nameField.getHeight() / 2);
		
		createButton = new Button("Create");
		createButton.setSize(buttonWidth, buttonHeight);
		createButton.setPosition(borderPadding * 3, nameLabel.getY() - buttonHeight - 3 * borderPadding);
		
		createButton.addListener(new ActionListener() {
			@Override
			public void onAction(ActionEvent event) {
				boolean success = createNewLevel(manager.getEditor());
				if(success) {
					manager.switchAction(EditorActions.SELECT);
				}
			}
		});
		
		cancelButton = new Button("Cancel");
		cancelButton.setSize(buttonWidth, buttonHeight);
		cancelButton.setPosition(width - 3 * borderPadding - buttonWidth, createButton.getY());

		cancelButton.addListener(new ActionListener() {
			@Override
			public void onAction(ActionEvent event) {
				manager.switchAction(EditorActions.SELECT);
			}
		});
		
		invalidNameLabel = new Label("Invalid Name");
		invalidNameLabel.setFont(loader.getFont(AssetLoader.font12));
		invalidNameLabel.setForegroundColor(Color.RED);
		invalidNameLabel.autoSetSize();
		invalidNameLabel.setPosition(nameField.getX(), nameField.getY() - invalidNameLabel.getHeight() - 5);
		invalidNameLabel.setVisible(false);
		
		add(titleLabel);
		add(nameLabel);
		add(nameField);
		add(createButton);
		add(cancelButton);
		add(invalidNameLabel);

		Color color = new Color(Color.BLACK).mul(1.0f, 1.0f, 1.0f, 0.5f);
		setBackgroundColor(color);
	}
	
	private boolean createNewLevel(LevelEditor editor) {
		String levelName = nameField.getText();
		
		if(!validName(levelName)){
			invalidNameLabel.setText("Invalid name");
			invalidNameLabel.setVisible(true);
			invalidNameLabel.autoSetSize();
			nameField.requestFocus();
			return false;
		} 

		if(LevelUtils.levelExists(levelName)) {
			invalidNameLabel.setText("Level already exists");
			invalidNameLabel.setVisible(true);
			invalidNameLabel.autoSetSize();
			nameField.requestFocus();
			return false;
		}
		
		// Create a new level
		Level level = new Level();
		level.setName(levelName);
		level.setManager(editor.getCurrentLevel().getManager());

		editor.setCurrentLevel(level);
		
		return true;
	}
	
	private boolean validName(String name) {
		String regex = "^[a-z][a-z0-9\\-_]*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(name);
		return match.find();
	}
	
}
