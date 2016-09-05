package neos.app.gui;

public interface ProgressMornitor {
	void setMessage(String mess);
	void setProgress(boolean indetermin);
	void setProgress(int n);
}
