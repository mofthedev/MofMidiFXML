package mofmidifxml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.*;

/**
 *
 * @author MOF
 */
public class MofMidiEmulator
{
    MidiChannel[] mChannels;
    MidiDevice.Info[] infoA;
    public int channelno;
    public int selectedDevice;
    
    double pitchvalue;
    int pitchvalue2;
    
    MidiDevice MidiOutDevice;
    Sequencer MidiOutSequencer;
    Transmitter seqTransmitter;
    Synthesizer midiSynth;
    Receiver machineReceiver;
    
    Map<Integer, Integer> pressedTunes;
    
    //starting from C4 (4th octave)
    public double[] freqList = {
    //281.60,//0
    294.11,//1
    308.92,//2
    //319.55,//3
    //328.57,//4
    330.88,//5
    348.23,//6
    //359.67,//7
    //362.62,//8
    367.05,//9
    //373.85,//10
    392.15,//11
    411.49,//12
    //424.23,//13
    //427.80,//14
    439.99,//15
    463.85,//16
    //481.41,//17
    //491.13,//18
    496.32,//19
    522.87,//20
    //537.43,//21
    //546.29,//22
    550.58//23
    };
    public double[] mts = {
    281.60,
    294.11,
    308.92,
    319.55,
    328.57,
    330.88,
    348.23,
    359.67,
    362.62,
    367.05,
    373.85,
    392.15,
    411.49,
    424.23,
    427.80,
    439.99,
    463.85,
    481.41,
    491.13,
    496.32,
    522.87,
    537.43,
    546.29,
    550.58
    };
    public double[] microtones = {
    281.606416318516,
    294.11764735126997,
    308.92910331717906,
    319.55838832720576,
    328.5703534842914,
    330.8823525651737,
    348.2373199782753,
    359.6713879512031,
    362.6251322276069,
    367.0588230280748,
    373.8554413435828,
    392.15686275902397,
    411.4959590574865,
    424.239772936163,
    427.8074863385694,
    439.99999983288757,
    463.8548111631015,
    481.4143789271389,
    491.1306294284758,
    496.3235294117646,
    522.8758173880346,
    537.4310057235962,
    546.294382896056,
    550.5882356701202
    };
    
    public double KeyNo2Freq(int k)
    {
        int octave = k / 12;
        int basek = k%12;
        double totheoct = Math.pow(2, octave);
        double rt = (freqList[basek]/16.0d) * totheoct;
        //System.out.println("k "+k+" | octave "+octave+" | basek "+basek);
        //System.out.println("rt: "+rt);
        return rt;
    }
    public void PlayKey(int k, boolean microtonal)
    {
        //System.out.println("PLAY "+k);
        if(pressedTunes.containsKey(k))
        {
            //System.out.println("Var: "+noteno);
            return;
        }
        else
        {
            //System.out.println("Yok: "+noteno);
        }
        
        channelno = k%12;
        if(microtonal)
        {
            PlayFreq(KeyNo2Freq(k));
        }
        else
        {
            PlayTune(k);
        }
        
        pressedTunes.put(k,channelno);
        //MoveChannel();
    }
    public void StopKey(int k, boolean microtonal)
    {
        //System.out.println("STOP "+k);
        Integer channeltostop_ = pressedTunes.get(k);
        if (channeltostop_ == null)
        {
            return;
        }
        /*int currentchannel = channelno;
        Integer channeltostop_ = pressedTunes.get(k);
        if(channeltostop_==null)
        {
            return;
        }
        int channeltostop = channeltostop_;
        channelno = channeltostop;*/
        channelno = channeltostop_;
        if(microtonal)
        {
            StopFreq(KeyNo2Freq(k));
        }
        else
        {
            StopTune(k);
        }
        //channelno = currentchannel;
        pressedTunes.remove(k);
        //MoveChannel();
    }
    
    public void MoveChannel()
    {
        channelno += 1;
        if(channelno >= 16)
        {
            channelno = 0;
        }
    }
    
    public MofMidiEmulator()
    {
        channelno = 0;
        CreateVirtualDevice(11);
    }
    
    public MofMidiEmulator(int device, int channel)
    {
        channelno = channel;
        CreateVirtualDevice(device);
    }
    
    public void CreateSelfMidi()
    {
        try
        {
            midiSynth = MidiSystem.getSynthesizer();
            midiSynth.open();

            //get and load default instrument and channel lists
            Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
            mChannels = midiSynth.getChannels();

            midiSynth.loadInstrument(instr[0]);//load an instrument
        }
        catch (MidiUnavailableException e)
        {
            
        }
    }
    
    public void CreateVirtualDevice(int device)
    {
        pitchvalue = 0.5f;
        pressedTunes = new HashMap<Integer,Integer>();
        try
        {
            infoA = MidiSystem.getMidiDeviceInfo();//array where all the device info goes
            int d=device;
            selectedDevice = d;
            System.out.println("Midi Device Number: "+infoA.length);
            for (int x=0;x<infoA.length;x++)
            {
                System.out.println("input "+x+" "+infoA[x]); //this displays all the devices
            }
            //MidiSystem.getMidiDevice(infoA[d]); //d is set to an integer that represents the item number on the device list of the device I wanna send MIDI to
            System.out.println("Selected: "+infoA[d]);//last check if the correct device is selected
            MidiOutDevice = MidiSystem.getMidiDevice(infoA[d]); //setting this to "my" device in order to set the Receiver
            
            if(!MidiOutDevice.isOpen())
            {
                MidiOutDevice.open();
            }
            machineReceiver = MidiOutDevice.getReceiver();
            
            MidiOutSequencer = MidiSystem.getSequencer(); 
            seqTransmitter = MidiOutSequencer.getTransmitter();
            seqTransmitter.setReceiver(machineReceiver); //probably unnecessary last 2 line but I gave this a try in case it helps 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void PlayNote(int noteno)
    {
        try
        {
            mChannels[0].noteOn(noteno, 100);//On channel 0, play note number 60 with velocity 100

        }
        catch (Exception e)
        {
            
        }
    }
    
    public void StopNote(int noteno)
    {
        try
        {
            mChannels[0].noteOff(noteno);//turn of the note

        }
        catch (Exception e)
        {
            
        }
    }
    
    public void PlayTune(int noteno)
    {
        
        try
        {
            ShortMessage myMsg = new ShortMessage();
            // Start playing the note Middle C (60), 
            // moderately loud (velocity = 93).
            myMsg.setMessage(ShortMessage.NOTE_ON, channelno, noteno, 100);
            long timeStamp = -1;
            //Receiver rcvr = MidiSystem.getReceiver();
            //rcvr.send(myMsg, timeStamp);
            
            
            machineReceiver.send(myMsg, timeStamp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public void StopTune(int noteno)
    {
        try
        {
            ShortMessage myMsg = new ShortMessage();
            myMsg.setMessage(ShortMessage.NOTE_OFF, channelno, noteno, 100);
            long timeStamp = -1;
            machineReceiver.send(myMsg, timeStamp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //0.0 - 1.0
    public void PitchBend(double newpitchvalue, int ch)
    {
        //System.out.println("Wow "+newpitchvalue+" "+pitchvalue);
        if(newpitchvalue == pitchvalue)
        {
            return;
        }
        pitchvalue = newpitchvalue;
        int newpitchdata = (short)(newpitchvalue * 8192.0f * 2);
        if(newpitchdata > 0 && newpitchdata!=8192.0f){newpitchdata -= 1;}
        int data1 = (newpitchdata & 0x7F);//0x3f
        int data2 = (newpitchdata >> 7);//7
        if(data2 > 127){data2 = 0;}
        if(data1 > 127){data1 = 127;}
        //System.out.println("NewPitchData: "+newpitchdata+ " | Data1: "+data1+ " | Data2: "+data2);
        try
        {
            ShortMessage myMsg = new ShortMessage();
            myMsg.setMessage(ShortMessage.PITCH_BEND, ch, data1, data2);//channelno
            long timeStamp = -1;
            machineReceiver.send(myMsg, timeStamp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void PitchBend(double newpitchvalue)
    {
        PitchBend(newpitchvalue, 0);
    }
    
    
    public void PlayFreq(double frq)
    {
        int[] np = Freq2MidiNote(frq);
        PitchBend2(np[1]);
        PlayTune(np[0]);
    }
    public void StopFreq(double frq)
    {
        int[] np = Freq2MidiNote(frq);
        //PitchBend2(8192);
        StopTune(np[0]);
    }
    
    //0.0 - 8192 - 16384
    public void PitchBend2(int newpitchvalue)
    {
        //System.out.println("Wow "+newpitchvalue+" "+pitchvalue);
        if(newpitchvalue == pitchvalue2)
        {
            return;
        }
        pitchvalue2 = newpitchvalue;
        int data1 = getLSB(newpitchvalue);
        int data2 = getMSB(newpitchvalue);
        //System.out.println("NewPitchData: "+newpitchdata+ " | Data1: "+data1+ " | Data2: "+data2);
        try
        {
            ShortMessage myMsg = new ShortMessage();
            myMsg.setMessage(ShortMessage.PITCH_BEND, 0, data1, data2);//channelno
            long timeStamp = -1;
            machineReceiver.send(myMsg, timeStamp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public byte getLSB(int value)
    {
    	return (byte)(value & 0x7F);
    }
    
    public byte getMSB(int value)
    {
    	return (byte)((value >> 7) & 0x7F);    	
    }
    
    /**
     * Converts the given frequency to a music string that involves
     * the Pitch Wheel and notes to create the frequency
     * @param freq the frequency
     * @return a MusicString that represents the frequency
     */
    public int[] Freq2MidiNote(double frequency)
    {
        double totalCents = 1200.0 * Math.log(frequency / 16.3515978312876) / Math.log(2);
        int octave = (int)(totalCents / 1200.0);
        double semitoneCents = totalCents - (octave * 1200.0);
        int semitone = (int)(semitoneCents / 100.0);
        double microtonalAdjustment = semitoneCents - (semitone * 100.0);
        int pitches = (int)(8192 + (microtonalAdjustment * 8192.0 / 100.0));
        
        if (pitches >= 12288)
	{
            int diff = 16384-pitches;
            int applieddiff = 8192-diff;
            pitches = applieddiff;
            semitone += 1;
            if (semitone == 12)
            {
                octave += 1;
                semitone = 0;
            }
        }
        
        // If we're close enough to the next note, just use the next note. 
        /*if (pitches >= 16384)
	{
            pitches = 8192;
            semitone += 1;
            if (semitone == 12)
            {
                octave += 1;
                semitone = 0;
            }
        }*/

        int note = ((octave)*12)+semitone; // This gives a MIDI value, 0 - 128
        if (note > 127) note = 127;
        
        int[] rt = {note, pitches};
        return rt;
        /*StringBuilder buddy = new StringBuilder();
        if (pitches > 0)
	{
            buddy.append(":PitchWheel(");
            buddy.append((int)pitches);
            buddy.append(") ");
        }
        buddy.append((int)note);
        if (pitches > 0)
	{
            buddy.append(" :PitchWheel(8192)"); // Reset the pitch wheel.  8192 = original pitch wheel position
        }
        return buddy.toString();*/
    }
}
