package chap18;
import javax.swing.*;
import java.io.*;

public interface Service extends Serializable { 
    public JPanel getGuiPanel();
}