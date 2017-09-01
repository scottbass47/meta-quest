package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.gui.ActionEvent;
import com.fullspectrum.gui.ActionListener;
import com.fullspectrum.gui.Button;
import com.fullspectrum.gui.Container;
import com.fullspectrum.gui.Label;
import com.fullspectrum.gui.TextField;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.editor.action.ActionManager;
import com.fullspectrum.editor.action.EditorActions;
import com.fullspectrum.level.LevelUtils;

public class OpenLevelPanel extends Container {

	private Texture background;
	private Label nameLabel;
	private Label titleLabel;
	private TextField nameField;
	private Button loadButton;
	private Button cancelButton;
	private int borderPadding = 10;
	private int buttonWidth = 100;
	private int buttonHeight = 40;
	private Label invalidNameLabel;
	
	public OpenLevelPanel(final ActionManager manager) {
		setSize(300, 150);
		
		AssetLoader loader = AssetLoader.getInstance();
		
		titleLabel = new Label("Open Level");
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
		
		loadButton = new Button("Load");
		loadButton.setSize(buttonWidth, buttonHeight);
		loadButton.setPosition(borderPadding * 3, nameLabel.getY() - buttonHeight - 3 * borderPadding);
		
		loadButton.addListener(new ActionListener() {
			@Override
			public void onAction(ActionEvent event) {
				boolean success = loadLevel(manager.getEditor());
				if(success) {
					manager.switchAction(EditorActions.SELECT);
				}
			}
		});
		
		cancelButton = new Button("Cancel");
		cancelButton.setSize(buttonWidth, buttonHeight);
		cancelButton.setPosition(width - 3 * borderPadding - buttonWidth, loadButton.getY());

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
		
		add(titleLabel);
		add(nameLabel);
		add(nameField);
		add(loadButton);
		add(cancelButton);
		add(invalidNameLabel);
		
		drawBackground();
	}
	
	private boolean loadLevel(LevelEditor editor) {
		String levelName = nameField.getText();
		
		if(!LevelUtils.levelExists(levelName)) {
			invalidNameLabel.setText("No level found");
			invalidNameLabel.setVisible(true);
			invalidNameLabel.autoSetSize();
			nameField.requestFocus();
			return false;
		}
		
		// Create a new level
		editor.setCurrentLevel(LevelUtils.loadLevel(editor.getCurrentLevel().getManager(), levelName));
		return true;
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
