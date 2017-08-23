package com.fullspectrum.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.editor.action.ActionManager;
import com.fullspectrum.editor.action.EditorActions;
import com.fullspectrum.gui.ActionEvent;
import com.fullspectrum.gui.ActionListener;
import com.fullspectrum.gui.Button;
import com.fullspectrum.gui.Container;
import com.fullspectrum.gui.Label;
import com.fullspectrum.gui.TextField;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.LevelUtils;

public class NewLevelPanel extends Container {

	private Texture background;
	private Label nameLabel;
	private TextField nameField;
	private Button createButton;
	private Button cancelButton;
	private int borderPadding = 10;
	private int buttonWidth = 100;
	private int buttonHeight = 40;
	private Label invalidNameLabel;
	
	public NewLevelPanel(final ActionManager manager) {
		setSize(300, 120);
		
		AssetLoader loader = AssetLoader.getInstance();
		
		nameLabel = new Label("Name: ");
		nameLabel.setFont(loader.getFont(AssetLoader.font18));
		nameLabel.autoSetSize();
		nameLabel.setPosition(borderPadding, height - nameLabel.getHeight() - 2 * borderPadding);
		
		nameField = new TextField();
		nameField.setBackgroundColor(Color.BLACK);
		nameField.setSize(width - 2 * borderPadding - (nameLabel.getX() + nameLabel.getWidth()), 30);
		nameField.setPosition(nameLabel.getX() + nameLabel.getWidth() + 5, nameLabel.getY() + nameLabel.getHeight() / 2 - nameField.getHeight() / 2);
		
		createButton = new Button("Create");
		createButton.setSize(buttonWidth, buttonHeight);
		createButton.setPosition(borderPadding * 3, nameLabel.getY() - nameLabel.getHeight() - buttonHeight - 20);
		
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
		cancelButton.setPosition(width - 3 * borderPadding - buttonWidth, nameLabel.getY() - nameLabel.getHeight() - buttonHeight - 20);

		cancelButton.addListener(new ActionListener() {
			@Override
			public void onAction(ActionEvent event) {
				manager.switchAction(EditorActions.SELECT);
			}
		});
		
		invalidNameLabel = new Label("Invalid Name");
		invalidNameLabel.setFont(loader.getFont(AssetLoader.font12));
		invalidNameLabel.setFontColor(Color.RED);
		invalidNameLabel.autoSetSize();
		invalidNameLabel.setPosition(nameField.getX(), nameField.getY() - invalidNameLabel.getHeight() - 5);
		invalidNameLabel.setVisible(false);
		
		add(nameLabel);
		add(nameField);
		add(createButton);
		add(cancelButton);
		add(invalidNameLabel);
		giveFocus(nameField);
		
		drawBackground();
	}
	
	private boolean createNewLevel(LevelEditor editor) {
		String levelName = nameField.getText();
		
		if(!validName(levelName)){
			invalidNameLabel.setText("Invalid name");
			invalidNameLabel.setVisible(true);
			invalidNameLabel.autoSetSize();
			giveFocus(nameField);
			return false;
		} 

		if(LevelUtils.levelExists(levelName)) {
			invalidNameLabel.setText("Level already exists");
			invalidNameLabel.setVisible(true);
			invalidNameLabel.autoSetSize();
			giveFocus(nameField);
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
	
	private void drawBackground() {
		Pixmap pix = new Pixmap(width, height, Format.RGBA8888);
		pix.setColor(new Color(Color.BLACK).mul(1.0f, 1.0f, 1.0f, 0.9f));
		pix.fill();
		
		background = new Texture(pix);
		pix.dispose();
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(background, x, y);

		super.render(batch);
	}
}
