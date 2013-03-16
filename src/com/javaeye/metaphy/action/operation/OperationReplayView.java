/**
 * Author by metaphy
 * Oct 28, 2010
 * All Rights Reserved.
 */
package com.javaeye.metaphy.action.operation;

import java.awt.event.ActionEvent;

import com.javaeye.metaphy.action.BaseAction;
import com.javaeye.metaphy.action.operation.OperationButton.Operations;
import com.javaeye.metaphy.action.util.GameSwingExecutor;
import com.javaeye.metaphy.game.GamePanel;
import com.javaeye.metaphy.game.ReplayView;
import com.javaeye.metaphy.model.BaseElement;
import com.javaeye.metaphy.model.Coordinate;
import com.javaeye.metaphy.threads.Move;
import com.javaeye.metaphy.threads.RunnableSingleton;

public class OperationReplayView extends BaseAction {
	private boolean next = true;

	public OperationReplayView() {
	}

	public OperationReplayView(boolean next) {
		this.next = next;
	}

	public void actionPerformed(ActionEvent e) {
		GamePanel panel = game.getPanel();
		ReplayView replayView = game.getReplayView();

		if (next) { // the next step
			int[] step = replayView.nextStep();

			if (step[0] == -1) { // Replay end
				panel.enableOneOpButton(Operations.NEXT_STEP, false);
			} else if (step[0] == 0) { // One pass
				game.getTimer().changeCurrentPlayer();
			} else {
				panel.enableOneOpButton(Operations.NEXT_STEP, false);

				BaseElement b1 = panel.getBaseElement(new Coordinate(step[0]));
				BaseElement b2 = panel.getBaseElement(new Coordinate(step[1]));
				// Parameters validation end
				Move moveRunnable = new Move();
				RunnableSingleton.instance().setMoveRunnable(moveRunnable);
				moveRunnable.setAppropriateMovable(b1);
				moveRunnable.setAppropriateMovable(b2);
				GameSwingExecutor.instance().execute(moveRunnable);
			}
		} else { // the previous step

		}

	}

	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

}
