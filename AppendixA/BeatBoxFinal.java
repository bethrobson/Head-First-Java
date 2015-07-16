package AppendixA;
// chapter 14

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.io.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;


public class BeatBoxFinal {  // implements MetaEventListener 

      JPanel mainPanel;
      JList incomingList;
      JTextField userMessage;
      ArrayList<JCheckBox> checkboxList;
      int nextNum;
      ObjectInputStream in;
      ObjectOutputStream out;
      Vector<String> listVector = new Vector<String>();
      String userName ;
      HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();
      Sequencer sequencer;
      Sequence sequence;
      Sequence mySequence = null;
      Track track;
      JFrame theFrame;

      String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", 
         "Open Hi-Hat","Acoustic Snare", "Crash Cymbal", "Hand Clap", 
         "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", 
         "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", 
         "Open Hi Conga"};
      int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
    

      public static void main (String[] args) {
        new BeatBoxFinal().startUp(args[0]);
      }
      
      public void startUp(String name) {
          userName = name;
          try {
              Socket sock = new Socket("127.0.0.1", 4242);
              out = new ObjectOutputStream(sock.getOutputStream());
              in = new ObjectInputStream(sock.getInputStream());
              Thread remote = new Thread(new RemoteReader());
              remote.start();
          }
          catch (Exception ex) {
              System.out.println("couldn't connect - you'll have to play alone.");
          }
          setUpMidi();
          buildGUI();
      }

      public void buildGUI() {
          theFrame = new JFrame("Cyber BeatBox");
          theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          BorderLayout layout = new BorderLayout();
          JPanel background = new JPanel(layout);
          background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

          checkboxList = new ArrayList<JCheckBox>();
          Box buttonBox = new Box(BoxLayout.Y_AXIS);

          JButton start = new JButton("Start");
          start.addActionListener(new MyStartListener());
          buttonBox.add(start);
          
          
          JButton stop = new JButton("Stop");
          stop.addActionListener(new MyStopListener());
          buttonBox.add(stop);

          JButton upTempo = new JButton("Tempo Up");
          upTempo.addActionListener(new MyUpTempoListener());
          buttonBox.add(upTempo);

           JButton downTempo = new JButton("Tempo Down");
          downTempo.addActionListener(new MyDownTempoListener());
          buttonBox.add(downTempo);
          
          JButton sendIt = new JButton("sendIt");
          sendIt.addActionListener(new MySendListener());
          buttonBox.add(sendIt);
          

          JButton saveIt = new JButton("Serialize It");  // new button
          saveIt.addActionListener(new MySendListener());
          buttonBox.add(saveIt);
          
          userMessage = new JTextField();
          buttonBox.add(userMessage);
          
          incomingList = new JList();
          incomingList.addListSelectionListener(new MyListSelectionListener());
          incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          JScrollPane theList = new JScrollPane(incomingList);
          buttonBox.add(theList);
          incomingList.setListData(listVector);
          
          Box nameBox = new Box(BoxLayout.Y_AXIS);
          for (int i = 0; i < 16; i++) {
              nameBox.add(new Label(instrumentNames[i]));
          }
        
          background.add(BorderLayout.EAST, buttonBox);
          background.add(BorderLayout.WEST, nameBox);

          theFrame.getContentPane().add(background);
          
          GridLayout grid = new GridLayout(16,16);
          grid.setVgap(1);
          grid.setHgap(2);
          mainPanel = new JPanel(grid);
          background.add(BorderLayout.CENTER, mainPanel);


          for (int i = 0; i < 256; i++) {                    
                JCheckBox c = new JCheckBox();
                c.setSelected(false);
                checkboxList.add(c);
                mainPanel.add(c);            
          } // end loop

          theFrame.setBounds(50,50,300,300);
          theFrame.pack();
          theFrame.setVisible(true);
        } // close method


     public void setUpMidi() {
       try {
        sequencer = MidiSystem.getSequencer();
        sequencer.open();
        // sequencer.addMetaEventListener(this);
        sequence = new Sequence(Sequence.PPQ,4);
        track = sequence.createTrack();
        sequencer.setTempoInBPM(120);
        
       } catch(Exception e) {e.printStackTrace();}
    } // close method

    public void buildTrackAndStart()
    {
        // this will hold the instruments for each vertical column,
        // in other words, each tick (may have multiple instruments)
        ArrayList<Integer> trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();


        for (int i = 0; i < 16; i++){
            trackList = new ArrayList<Integer>();
            for (int j = 0; j < 16; j++){
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16 * i));
                if (jc.isSelected()){
                    int key = instruments[i];
                    trackList.add(key);
                } 
                else
                {
                    trackList.add(null);
                }
            } // close inner

            makeTracks(trackList);
        } // close outer

     track.add(makeEvent(192,9,1,0,15)); // - so we always go to full 16 beats
               
   
      
       try {
           
           sequencer.setSequence(sequence);  
           sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);                  
           sequencer.start();
           sequencer.setTempoInBPM(120);
       } catch(Exception e) {e.printStackTrace();}
 
      } // close method
            
//============================================================== inner class listeners           
       
      public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
             buildTrackAndStart();
         }
      }

    public class MyStopListener implements ActionListener {
       public void actionPerformed(ActionEvent a) {
           sequencer.stop();
       }
    }

    public class MyUpTempoListener implements ActionListener {
       public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * 1.03));
       }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * .97));
        }
    }

    public class MySendListener implements ActionListener {    // new - save
       public void actionPerformed(ActionEvent a) {
          // make an arraylist of just the STATE of the checkboxes
         boolean[] checkboxState = new boolean[256];

         for (int i = 0; i < 256; i++) {
             JCheckBox check = (JCheckBox) checkboxList.get(i);
             if (check.isSelected()) {
                checkboxState[i] = true;
             }
          }

         try {
             out.writeObject(userName + nextNum++ + ": " + userMessage.getText());
            out.writeObject(checkboxState);
         } catch(Exception ex) {
             ex.printStackTrace();
             System.out.println("sorry dude. Could not send it to the server");
         }

       } // close method
     } // close inner class

    public class MyListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent le) {
            if (!le.getValueIsAdjusting()) {
                String selected = (String) incomingList.getSelectedValue();
                if (selected != null) {
                    boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected);
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                }
            }
        }
    }
    
    public class RemoteReader implements Runnable {
        boolean[] checkboxState = null;
        String nameToShow = null;
        Object obj = null;
        
        public void run() {
            try {
                while ((obj=in.readObject()) != null) {
                    System.out.println("got an object from server");
                    System.out.println(obj.getClass());
                    String nameToShow = (String) obj;
                    checkboxState = (boolean[]) in.readObject();
                    otherSeqsMap.put(nameToShow, checkboxState);
                    listVector.add(nameToShow);
                    incomingList.setListData(listVector);
                }
            }catch (Exception e) { e.printStackTrace(); }
        }
    }


//==============================================================       

    public void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            }
            else
            {
                check.setSelected(false);
            }
        }
    }
    
     public void makeTracks(ArrayList<Integer> list) {
         Iterator it = list.iterator();
         for (int i = 0; i < 16; i++) {
             Integer num = (Integer) it.next();
             if (num != null) {
                 int numKey = num.intValue();
                 track.add(makeEvent(144, 9, numKey, 100, i));
                 track.add(makeEvent(128, 9, numKey, 100, i+1));
             }
         }
      }
        


     public  MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
          MidiEvent event = null;
          try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
            
            }catch(Exception e) { }
          return event;
       }
}
          
          