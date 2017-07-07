package pasjans;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.TableView;
import javafx.scene.shape.Rectangle;

public class Table {
	private int x;
	private int y;
	private int width;
	private int height;
	private int arcWidth;
	private int arcHeight;
	
	private TableView<UndoSteps> table = new TableView<UndoSteps>();
		
	public Table(){
		
	}
	
	public Table(Group root, int x, int y, int width, int height, int arcWidth, int arcHeight, ObservableList<UndoSteps> undos){
		Rectangle rectangle = new Rectangle();
		this.width = width;
		this.height = height;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		rectangle.setX(x);
		rectangle.setY(y);
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		rectangle.setArcWidth(arcWidth);
		rectangle.setArcHeight(arcHeight);
		root.getChildren().add(rectangle);
		ObservableList<UndoSteps> undoSteps = undos;
	}
	
	
}
