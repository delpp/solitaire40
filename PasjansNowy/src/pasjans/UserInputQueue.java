package pasjans;

import java.util.ArrayDeque;
import javafx.scene.input.MouseEvent;

public class UserInputQueue {
	ArrayDeque<MouseEvent> mouseEvent;
	
	
	public UserInputQueue(){
		mouseEvent = new ArrayDeque<MouseEvent>();
	}
	
	public void addMouseEvent(MouseEvent event){
		mouseEvent.add(event);
	}
	
	public MouseEvent getMouseEvent(){
		if (mouseEvent.size() > 0) return mouseEvent.poll();
		return null;
		
	}
}
