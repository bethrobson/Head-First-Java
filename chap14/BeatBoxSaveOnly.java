package chap14;
// chapter 14

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;


public class BeatBoxSaveOnly {  // implements MetaEventListener 

      JPanel mainPanel;
      ArrayList<JCheckBox> checkboxList;
      // int bpm = 120;
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
        new BeatBoxSaveOnly().buildGUI();
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

          JButton saveIt = new JButton("Serialize It");  // new button
          saveIt.addActionListener(new MySendListener());
          buttonBox.add(saveIt);

          JButton restore = new JButton("Restore");     // new button
          restore.addActionListener(new MyReadInListener());
          buttonBox.add(restore);

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

          setUpMidi();

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
/*
     public class MyCheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent ev) {      
           // might add real-time removal or addition, probably not because of timing
        }
     } // close inner class
*/

     public void buildTrackAndStart() {
        // this will hold the instruments for each vertical column,
        // in other words, each tick (may have multiple instruments)
        int[] trackList = null;
     
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        

      for (int i = 0; i < 16; i++) {
         trackList = new int[16];

         int key = instruments[i];

         for (int j = 0; j < 16; j++ ) {         
               JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i));
               
              if ( jc.isSelected()) {
                 trackList[j] = key;
              } else {
                 trackList[j] = 0;
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
            FileOutputStream fileStream = new FileOutputStream(
                new File("Checkbox.ser"));
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(checkboxState);
         } catch(Exception ex) {
             ex.printStackTrace();
         }

       } // close method
     } // close inner class



    public class MyReadInListener implements ActionListener {  // new - restore
        public void actionPerformed(ActionEvent a) {
          // read in the thing

          boolean[] checkboxState = null;
          try {
              FileInputStream fileIn = new FileInputStream(
                  new File("Checkbox.ser"));
              ObjectInputStream is = new ObjectInputStream(fileIn);
              checkboxState = (boolean[]) is.readObject();

          } catch(Exception ex) {ex.printStackTrace();}

              // now reset the sequence to be this
          for (int i = 0; i < 256; i++) {
             JCheckBox check = (JCheckBox) checkboxList.get(i);
             if (checkboxState[i]) {
                check.setSelected(true);
             } else {
                check.setSelected(false);
             }
         } 

       

        // now stop sequence and restart
        sequencer.stop(); 
        buildTrackAndStart();
      } // close method
  } // close inner class


//==============================================================       

     public void makeTracks(int[] list) {

         for (int i = 0; i < 16; i++) {
           int key = list[i];

           if (key != 0) {
               track.add(makeEvent(144,9,key, 100, i));
               track.add(makeEvent(128,9,key, 100, i + 1));
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

/*
        public void meta(MetaMessage message) {
            if (message.getType() == 47) {
                sequencer.start();
                sequencer.setTempoInBPM(bpm);
            }
       }
*/


   } // close class

        
             
          
          
          