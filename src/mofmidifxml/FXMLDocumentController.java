/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mofmidifxml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 *
 * @author MOF
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    AnchorPane anchorPane;

    MofMidiEmulator emulator, emulator2, emulator3;
    TouchOnNode[] touchpoints;
    ArrayList<Node> keyboardNodes1, keyboardNodes2, controllerNodes, stringNodes;
    boolean activepitch, microtonal;

    StringProperty stylePropertySlider;
    StringProperty stylePropertyBackground, stylePropertyDefault, stylePropertyWhite, stylePropertyBlack;
    Glow glow, glowPressed;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("Mof Virtual Midi Controller v1.1");
        emulator = new MofMidiEmulator(11, 0);
        emulator2 = new MofMidiEmulator(12, 1);
        emulator3 = new MofMidiEmulator(13, 2);

        stylePropertyBackground = new SimpleStringProperty();
        stylePropertyBackground.setValue("-fx-background-color: #c9a3e2");
        stylePropertyDefault = new SimpleStringProperty();
        stylePropertyDefault.setValue("-fx-background-color: #e4d0ea");
        stylePropertyWhite = new SimpleStringProperty();
        stylePropertyWhite.setValue("-fx-background-color: #7c4c9b");
        stylePropertyBlack = new SimpleStringProperty();
        stylePropertyBlack.setValue("-fx-background-color: #582f72");

        glow = new Glow();
        glow.setLevel(0.5);
        glowPressed = new Glow();
        glowPressed.setLevel(0.1);

        keyboardNodes1 = new ArrayList<Node>();
        keyboardNodes2 = new ArrayList<Node>();
        CreateKeyboard(0, 70, 120, 400, 0, 108, keyboardNodes1, emulator);//120, 400
        CreateKeyboard(0, 550, 120, 400, 0, 108, keyboardNodes2, emulator2);
        
        controllerNodes = new ArrayList<Node>();
        CreateControllers();
        
        SlideKeyboard(keyboardNodes1, -4100);
        SlideKeyboard(keyboardNodes2, -3300);
        
        /*CreateKeyboard(0,  50, 60, 200, 36, 59);
        CreateKeyboard(0, 300, 60, 200, 60, 83);*/

        touchpoints = new TouchOnNode[500];

        anchorPane.setEffect(glowPressed);
        anchorPane.styleProperty().bind(stylePropertyBackground);
        
        activepitch=true;
        ActivepitchButton("AP", 1225, 10, 50, 50);
        
        microtonal=false;
        MicrotonalButton("MT", 1300, 10, 50, 50);
        
        
        //string part
//        stringNodes = new ArrayList<Node>();
//        
//        SlideDownButton("Ins2", 1400, 10, 50, 50, 1100, keyboardNodes1, keyboardNodes2, controllerNodes, stringNodes);
//        SlideUpButton("Ins1", 1400, 1110, 50, 50, 1100, keyboardNodes1, keyboardNodes2, controllerNodes, stringNodes);
//        
//        int stringBoardY = 1110;//1110
//        CreateString(50, stringBoardY+80, 1600, 100, 36, stringNodes, emulator3);
//        CreateString(50, stringBoardY+80+110, 1600, 100, 60, stringNodes, emulator3);
//        CreateString(50, stringBoardY+80+220, 1600, 100, 84, stringNodes, emulator3);
        
    }
    
    public Node pick(Node node, double sceneX, double sceneY)
    {
        Point2D p = node.sceneToLocal(sceneX, sceneY, true /* rootScene */);

        // check if the given node has the point inside it, or else we drop out
        if (!node.contains(p)) return null;

        // at this point we know that _at least_ the given node is a valid
        // answer to the given point, so we will return that if we don't find
        // a better child option
        if (node instanceof Parent)
        {
            // we iterate through all children in reverse order, and stop when we find a match.
            // We do this as we know the elements at the end of the list have a higher
            // z-order, and are therefore the better match, compared to children that
            // might also intersect (but that would be underneath the element).
            Node bestMatchingChild = null;
            List<Node> children = ((Parent)node).getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--)
            {
                Node child = children.get(i);
                p = child.sceneToLocal(sceneX, sceneY, true /* rootScene */);
                if (child.isVisible() && !child.isMouseTransparent() && child.contains(p)) {
                    bestMatchingChild = child;
                    break;
                }
            }

            if (bestMatchingChild != null)
            {
                return pick(bestMatchingChild, sceneX, sceneY);
            }
        }

        return node;
    }

    public void CreateKeyboard(int KeyboardPos_x, int KeyboardPos_y, int KeySize_w, int KeySize_h, int KeyNo_first, int KeyNo_last, ArrayList<Node> keyboardNodes, MofMidiEmulator emul)
    {
        int Keyboard_x = KeyboardPos_x;
        int Keyboard_y = KeyboardPos_y;
        int Key_w = KeySize_w;
        int Key_h = KeySize_h;

        int KeyNo_start = KeyNo_first;
        int KeyNo_end = KeyNo_last;

        int K_i = KeyNo_start;
        int i = 0;
        while (K_i <= KeyNo_end) {
            int offsetx = 0;
            int thish = Key_h - Key_h / 2;//Key_h;
            int thisy = Keyboard_y;
            int keyRelat = K_i % 12;
            int keyRelat2 = i / 12;
            
            if (keyRelat == 1 || keyRelat == 3 || keyRelat == 6 || keyRelat == 8 || keyRelat == 10)//if is a black key
            {
                //thish = Key_h - Key_h / 2;
            }
            else
            {
                thisy = Keyboard_y + Key_h / 2;
            }
            
            if (keyRelat >= 5)//if is part of the second half (F and right side of it)
            {
                offsetx = Key_w / 2;
            }
            offsetx += keyRelat2 * Key_w;

            Node newnode = CreatePad(Keyboard_x + (Key_w / 2) * i + offsetx, thisy, Key_w, thish, K_i, emul);
            keyboardNodes.add(newnode);

            K_i++;
            i++;
        }

        /*
        CreatePad(Keyboard_x,                    200,   200, 300,   48);
            CreatePad(Keyboard_x + (Key_w/2)*1,  50,    200, 300,   49);
        CreatePad(Keyboard_x + (Key_w/2)*2,      200,   200, 300,   50);
            CreatePad(Keyboard_x + (Key_w/2)*3,  50,    200, 300,   51);
        CreatePad(Keyboard_x + (Key_w/2)*4,      200,   200, 300,   52);
         */
    }

    public Node CreatePad(int x, int y, int w, int h, int KeyNo, MofMidiEmulator emul)
    {
        Pane newpane = new Pane();
        anchorPane.getChildren().add(newpane);
        newpane.setLayoutX(x);
        newpane.setLayoutY(y);
        newpane.setPrefSize(w, h);

        int keyRelat = KeyNo % 12;
        if (keyRelat == 1 || keyRelat == 3 || keyRelat == 6 || keyRelat == 8 || keyRelat == 10) {
            newpane.styleProperty().bind(stylePropertyBlack);
            newpane.toFront();
        } else {
            newpane.styleProperty().bind(stylePropertyWhite);
            newpane.toBack();
        }
        
        newpane.setId(""+KeyNo);
        newpane.setEffect(glow);

        /*newpane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                emulator.StopTune(KeyNo);
            }
        });*/

        newpane.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                event.consume();
                if(!newpane.isPressed())
                {
                    TouchPoint tp = event.getTouchPoint();
                    ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                    tplist.add(tp);
                    int tpid = tp.getId();
                    
                    double pX = tp.getSceneX();
                    double pY = tp.getSceneY();
                    double minX = newpane.getLayoutX();
                    double minY = newpane.getLayoutY();
                    double maxX = minX+newpane.getWidth();
                    double maxY = minY+newpane.getHeight();
                    
                    
                    if(touchpoints[tpid]!=null && !touchpoints[tpid].node.getId().equals(newpane.getId()))
                    {
                        Event anotherKeyEvent2 = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tplist, 2, false, false, false, false);
                        touchpoints[tpid].node.fireEvent(anotherKeyEvent2);
                    }

                    
                    touchpoints[tpid] = new TouchOnNode(tpid, tp.getSceneX(), tp.getSceneY(), newpane);
                    //emul.PlayTune(KeyNo);
                    emul.PlayKey(KeyNo, microtonal);
                    newpane.setEffect(glowPressed);
                    
                    if(activepitch)
                    {
                        double pbend = (1.0d-((pY-minY)/(maxY-minY)))/2.0d+0.25d;
                        emul.PitchBend(pbend, KeyNo%12);
                    }
                }
            }
        });

        newpane.setOnTouchReleased(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                event.consume();
                
                TouchPoint tp = event.getTouchPoint();
                int tpid = tp.getId();
                if(touchpoints[tpid]!=null)
                {
                    if(touchpoints[tpid].node.getId().equals(newpane.getId()))
                    {
                        touchpoints[tpid] = null;
                    }
                    else
                    {
                        ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                        tplist.add(tp);
                        
                        Event anotherKeyEvent2 = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tplist, 2, false, false, false, false);
                        touchpoints[tpid].node.fireEvent(anotherKeyEvent2);
                    }
                }
                
                
                //emul.StopTune(KeyNo);
                emul.StopKey(KeyNo, microtonal);
                newpane.setEffect(glow);
            }
        });
        
        newpane.setOnTouchMoved(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                event.consume();
                
                //System.out.println("Touch move on "+newpane.getId());
                
                TouchPoint p = event.getTouchPoint();
                double pX = p.getSceneX();
                double pY = p.getSceneY();
                
                double minX = newpane.getLayoutX();
                double minY = newpane.getLayoutY();
                double maxX = minX+newpane.getWidth();
                double maxY = minY+newpane.getHeight();
                
                if(pX < minX || pX > maxX || pY < minY ||pY > maxY)
                {
                    
                    if(newpane.isPressed())
                    {
                        //emul.StopTune(KeyNo);
                        emul.StopKey(KeyNo, microtonal);
                        newpane.setEffect(glow);
                    }
                    Node pick = pick(newpane.getParent(), pX, pY);
                    if(pick!=null && !pick.isPressed())
                    {
                        TouchPoint tp = p;//new TouchPoint(1, TouchPoint.State.PRESSED, p.getSceneX(), p.getSceneY(), p.getScreenX(), p.getScreenY(), (EventTarget) event.getSource(), null);
                        ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                        tplist.add(tp);
                        Event anotherKeyEvent = new TouchEvent(TouchEvent.TOUCH_PRESSED, tp, tplist, 1, false, false, false, false);
                        pick.fireEvent(anotherKeyEvent);
                        
                        /*int tpid = tp.getId();
                        if(touchpoints[tpid]==null)
                        {
                            System.out.println("Yeni sürüklendi "+tpid);
                            touchpoints[tpid] = new TouchOnNode(tpid, tp.getSceneX(), tp.getSceneY(), pick);
                        }
                        else
                        {
                            if(!touchpoints[tpid].node.getId().equals(pick.getId()))
                            {
                                Event anotherKeyEvent2 = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tplist, 2, false, false, false, false);
                                touchpoints[tpid].node.fireEvent(anotherKeyEvent2);
                                System.out.println("Sürüklendi "+tpid);
                                touchpoints[tpid] = null;
                                touchpoints[tpid] = new TouchOnNode(tpid, tp.getSceneX(), tp.getSceneY(), pick);
                            }
                            else
                            {
                                System.out.println("Aynı nesnede "+tpid);
                                System.out.println(""+touchpoints[tpid].node.toString());
                                System.out.println(""+pick.toString());
                            }
                        }*/
                    }
                    
                    /*else
                    {
                        emulator.PlayTune(KeyNo);
                    }*/
                    
                    /*Stage thestage = (Stage)newpane.getScene().getWindow();
                    
                    TouchPoint tp = new TouchPoint(1, TouchPoint.State.PRESSED, p.getSceneX(), p.getSceneY(), p.getScreenX(), p.getScreenY(), (EventTarget) event.getSource(), null);
                    ArrayList<TouchPoint> tplist = new ArrayList<>();
                    tplist.add(tp);
                    Event anotherKeyEvent = new TouchEvent(TouchEvent.TOUCH_PRESSED, tp, tplist, 1, false, false, false, false);
                    thestage.fireEvent(anotherKeyEvent);
                    */
                    
                    /*
                    thestage.addEventFilter(TouchEvent.TOUCH_PRESSED, new EventHandler<TouchEvent>() {
                        @Override
                        public void handle(TouchEvent event) {
                            event.consume();
                            
                            TouchPoint tp = new TouchPoint(1, TouchPoint.State.PRESSED, p.getSceneX(), p.getSceneY(), p.getScreenX(), p.getScreenY(), (EventTarget) event.getSource(), null);
                            ArrayList<TouchPoint> tplist = new ArrayList<>();
                            tplist.add(tp);
                            Event anotherKeyEvent = new TouchEvent(TouchEvent.TOUCH_PRESSED, tp, tplist, 1, false, false, false, false);
                            thestage.fireEvent(anotherKeyEvent);
                        }
                    });
                    */
                    
                }
                else
                {
                    //emul.PlayTune(KeyNo);
                    emul.PlayKey(KeyNo, microtonal);
                    newpane.setEffect(glowPressed);
                    
                    if(activepitch)
                    {
                        double pbend = (1.0d-((pY-minY)/(maxY-minY)))/2.0d+0.25d;
                        emul.PitchBend(pbend, KeyNo%12);
                    }
                    
                    TouchPoint tp = p;
                    int tpid = tp.getId();
                    ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                    tplist.add(tp);
                    if(touchpoints[tpid]!=null && !touchpoints[tpid].node.getId().equals(newpane.getId()))
                    {
                        Event anotherKeyEvent2 = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tplist, 5, false, false, false, false);
                        touchpoints[tpid].node.fireEvent(anotherKeyEvent2);
                        touchpoints[tpid] = null;
                    }
                    
                    touchpoints[tpid] = new TouchOnNode(tpid, tp.getSceneX(), tp.getSceneY(), newpane);


                }
            }
        });
        
        return newpane;
    }
    
    
    public Node CreateString(int x, int y, int w, int h, int stringNo, ArrayList<Node> keyboardNodes, MofMidiEmulator emul)
    {
        Pane newpane = new Pane();
        anchorPane.getChildren().add(newpane);
        newpane.setLayoutX(x);
        newpane.setLayoutY(y);
        newpane.setPrefSize(w, h);
        
        keyboardNodes.add(newpane);

        if (stringNo%2==0)
        {
            newpane.styleProperty().bind(stylePropertyBlack);
        }
        else
        {
            newpane.styleProperty().bind(stylePropertyWhite);
        }
        
        newpane.setId("strng "+stringNo);
        newpane.setEffect(glow);


        newpane.setOnTouchPressed(new EventHandler<TouchEvent>()
        {
            @Override
            public void handle(TouchEvent event)
            {
                event.consume();
                if(!newpane.isPressed())
                {
                    TouchPoint tp = event.getTouchPoint();
                    ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                    tplist.add(tp);
                    int tpid = tp.getId();
                    
                    double pX = tp.getSceneX();
                    double pY = tp.getSceneY();
                    double minX = newpane.getLayoutX();
                    double minY = newpane.getLayoutY();
                    double maxX = minX+newpane.getWidth();
                    double maxY = minY+newpane.getHeight();
                    
                    
                    if(touchpoints[tpid]!=null && !touchpoints[tpid].node.getId().equals(newpane.getId()))
                    {
                        Event anotherKeyEvent2 = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tplist, 2, false, false, false, false);
                        touchpoints[tpid].node.fireEvent(anotherKeyEvent2);
                    }

                    
                    touchpoints[tpid] = new TouchOnNode(tpid, tp.getSceneX(), tp.getSceneY(), newpane);
                    emul.PlayTune(stringNo);
                    newpane.setEffect(glowPressed);
                    
                    double pbend = ((pX-minX)/(maxX-minX));
                    emul.PitchBend(pbend, emul.channelno);//stringNo%12
                }
            }
        });

        newpane.setOnTouchReleased(new EventHandler<TouchEvent>()
        {
            @Override
            public void handle(TouchEvent event)
            {
                event.consume();
                
                TouchPoint tp = event.getTouchPoint();
                int tpid = tp.getId();
                if(touchpoints[tpid]!=null)
                {
                    if(touchpoints[tpid].node.getId().equals(newpane.getId()))
                    {
                        touchpoints[tpid] = null;
                    }
                    else
                    {
                        ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                        tplist.add(tp);
                        
                        Event anotherKeyEvent2 = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tplist, 2, false, false, false, false);
                        touchpoints[tpid].node.fireEvent(anotherKeyEvent2);
                    }
                }
                
                
                emul.StopTune(stringNo);
                newpane.setEffect(glow);
            }
        });
        
        newpane.setOnTouchMoved(new EventHandler<TouchEvent>()
        {
            @Override
            public void handle(TouchEvent event)
            {
                event.consume();
                
                
                TouchPoint p = event.getTouchPoint();
                double pX = p.getSceneX();
                double pY = p.getSceneY();
                
                double minX = newpane.getLayoutX();
                double minY = newpane.getLayoutY();
                double maxX = minX+newpane.getWidth();
                double maxY = minY+newpane.getHeight();
                
                if(pX < minX || pX > maxX || pY < minY ||pY > maxY)
                {
                    
                    if(newpane.isPressed())
                    {
                        emul.StopTune(stringNo);
                        newpane.setEffect(glow);
                    }
                    Node pick = pick(newpane.getParent(), pX, pY);
                    if(pick!=null && !pick.isPressed())
                    {
                        TouchPoint tp = p;//new TouchPoint(1, TouchPoint.State.PRESSED, p.getSceneX(), p.getSceneY(), p.getScreenX(), p.getScreenY(), (EventTarget) event.getSource(), null);
                        ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                        tplist.add(tp);
                        Event anotherKeyEvent = new TouchEvent(TouchEvent.TOUCH_PRESSED, tp, tplist, 1, false, false, false, false);
                        pick.fireEvent(anotherKeyEvent);
                        
                    }
                    
                }
                else
                {
                    //emul.PlayTune(stringNo);
                    newpane.setEffect(glowPressed);
                    
                    double pbend = ((pX-minX)/(maxX-minX));//(1.0d-((pX-minX)/(maxX-minX)))/2.0d+0.25d;
                    //System.out.println("PB: "+pbend);
                    emul.PitchBend(pbend, emul.channelno);//stringNo%12
                    System.out.println("CN: "+emul.channelno);
                    
                    TouchPoint tp = p;
                    int tpid = tp.getId();
                    ArrayList<TouchPoint> tplist = new ArrayList<TouchPoint>();
                    tplist.add(tp);
                    if(touchpoints[tpid]!=null && !touchpoints[tpid].node.getId().equals(newpane.getId()))
                    {
                        Event anotherKeyEvent2 = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tplist, 5, false, false, false, false);
                        touchpoints[tpid].node.fireEvent(anotherKeyEvent2);
                        touchpoints[tpid] = null;
                    }
                    
                    touchpoints[tpid] = new TouchOnNode(tpid, tp.getSceneX(), tp.getSceneY(), newpane);


                }
            }
        });
        
        return newpane;
    }
    
    public void CreateControllers()
    {
        ControllerSlide("<", 10,10,50,50,keyboardNodes1, -50);
        ControllerSlide(">", 70,10,50,50,keyboardNodes1, 50);
        ControllerSlide("<", 10,490,50,50,keyboardNodes2, -50);
        ControllerSlide(">", 70,490,50,50,keyboardNodes2, 50);
        
        ControllerMenuChannels(150, 10, 250, 50, emulator);
        ControllerMenuChannels(150, 490, 250, 50, emulator2);
        
        
        ControllerMenuDevices(450, 10, 250, 50, emulator);
        ControllerMenuDevices(450, 490, 250, 50, emulator2);
        
        ControllerPitchBender(750, 10, 400, 50, emulator);
        
    }
    
    public void ControllerPitchBender(int x, int y, int w, int h, MofMidiEmulator emul)
    {
        Pane slider = new Pane();

        anchorPane.getChildren().add(slider);
        slider.setLayoutX(x);
        slider.setLayoutY(y);
        slider.setPrefSize(w, h);
        
        slider.setEffect(glow);
        slider.styleProperty().bind(stylePropertyDefault);
        slider.toBack();
        
        slider.setId("SldrPitch "+x+" "+y);
        
        
        
        final double thumbW = h/3;
        final double thumbH = h;
        final double thumbOrigin = x+w/2-thumbW/2;
        final double thumbMinX = x;
        final double thumbMaxX = x+w-thumbW;
        final double thumbMaxValue = thumbMaxX - thumbMinX;
        
        Pane thumb = new Pane();

        anchorPane.getChildren().add(thumb);
        thumb.setLayoutX(thumbOrigin);
        thumb.setLayoutY(y);
        thumb.setPrefSize(thumbW, thumbH);
        
        thumb.setEffect(glow);
        thumb.styleProperty().bind(stylePropertyBlack);
        thumb.toFront();
        
        thumb.setId("SldrThumb "+x+" "+y);
        
        //activate bending & bend
        slider.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                event.consume();
                
                TouchPoint tp = event.getTouchPoint();
                double targetX = tp.getSceneX();
                
                double diffratio = ControllerGetSliderValue(slider, thumb, thumbMinX, thumbMaxX, targetX, thumbMaxValue);
                //System.out.println("Pitch: "+diffratio);
                emul.PitchBend(diffratio);
            }
        });
        //inactivate bending
        slider.setOnTouchReleased(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                event.consume();
                
                thumb.setLayoutX(thumbOrigin);
                emul.PitchBend(0.5f);
            }
        });
        //bend
        slider.setOnTouchMoved(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                event.consume();
                
                TouchPoint tp = event.getTouchPoint();
                double targetX = tp.getSceneX();
                
                double diffratio = ControllerGetSliderValue(slider, thumb, thumbMinX, thumbMaxX, targetX, thumbMaxValue);
                //System.out.println("Pitch: "+diffratio);
                emul.PitchBend(diffratio);
            }
        });
        
        
        controllerNodes.add(slider);
        controllerNodes.add(thumb);
    }
    
    //returns the value in scale of 0.0-1.0
    public double ControllerGetSliderValue(Node slider, Node thumb, double minX, double maxX, double currentX, double maxVal)
    {
        if(currentX < minX)
        {
            currentX = minX;
        }
        else if( currentX > maxX )
        {
            currentX = maxX;
        }

        thumb.setLayoutX(currentX);

        double diffr = currentX - minX;
        double diffratio = ( (double)diffr / (double)maxVal );
        return diffratio;
    }
    
    public void ControllerMenuChannels(int x, int y, int w, int h, MofMidiEmulator emul)
    {
        MenuButton menubutton = new MenuButton("Channel "+emul.channelno);
        anchorPane.getChildren().add(menubutton);
        
        for (int i = 0; i < 16; i++)
        {
            final int chno = i;
            MenuItem newitem = new MenuItem("Ch "+i);
            newitem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    emul.channelno = chno;
                    menubutton.setText("Channel "+emul.channelno);
                }
            });
            menubutton.getItems().add(newitem);
        }
        
        menubutton.setLayoutX(x);
        menubutton.setLayoutY(y);
        menubutton.setPrefSize(w, h);
        
        
        
        menubutton.setEffect(glow);
        menubutton.styleProperty().bind(stylePropertyDefault);
        menubutton.toBack();
        
        menubutton.setId("ChBtn "+x+" "+y);
        
        
        controllerNodes.add(menubutton);
    }
    
    
    public void ControllerMenuDevices(int x, int y, int w, int h, MofMidiEmulator emul)
    {
        MenuButton menubutton = new MenuButton("Device #"+emul.selectedDevice+" "+emul.infoA[emul.selectedDevice]);
        anchorPane.getChildren().add(menubutton);
        
        for (int i = 0; i < emul.infoA.length; i++)
        {
            final int dvno = i;
            MenuItem newitem = new MenuItem("Out "+i);
            newitem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    emul.CreateVirtualDevice(dvno);
                    menubutton.setText("Device #"+emul.selectedDevice+" "+emul.infoA[emul.selectedDevice]);
                }
            });
            menubutton.getItems().add(newitem);
        }
        
        menubutton.setLayoutX(x);
        menubutton.setLayoutY(y);
        menubutton.setPrefSize(w, h);
        
        
        menubutton.setEffect(glow);
        menubutton.styleProperty().bind(stylePropertyDefault);
        menubutton.toBack();
        
        menubutton.setId("ChBtn "+x+" "+y);
        
        
        controllerNodes.add(menubutton);
    }
    
    public void ControllerSlide(String text, int x, int y, int w, int h, ArrayList<Node> nodes, int movex)
    {
        Button newpane = new Button(text);
        anchorPane.getChildren().add(newpane);
        newpane.setLayoutX(x);
        newpane.setLayoutY(y);
        newpane.setPrefSize(w, h);

        newpane.setEffect(glow);
        newpane.styleProperty().bind(stylePropertyDefault);
        newpane.toBack();

        newpane.setId("SlideBtn "+x+" "+y);
        
        newpane.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                
                SlideKeyboard(nodes, movex);
            }
        });
        
        controllerNodes.add(newpane);
    }
    
    public void SlideKeyboard(ArrayList<Node> nodes, int x)
    {
        for (Node node : nodes)
        {
            double oldposx = node.layoutXProperty().get();
            node.layoutXProperty().set(oldposx+x);
        }
    }
    
    
    
    
    
    
    public void ActivepitchButton(String text, int x, int y, int w, int h)
    {
        Button newpane = new Button(text);
        anchorPane.getChildren().add(newpane);
        newpane.setLayoutX(x);
        newpane.setLayoutY(y);
        newpane.setPrefSize(w, h);
        
        if(activepitch)
        {
            newpane.setEffect(glowPressed);
        }
        else
        {
            newpane.setEffect(glow);
        }

        newpane.styleProperty().bind(stylePropertyDefault);
        newpane.toBack();

        newpane.setId("APBtn "+x+" "+y);
        
        newpane.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                activepitch = !activepitch;
                if(activepitch)
                {
                    newpane.setEffect(glowPressed);
                }
                else
                {
                    newpane.setEffect(glow);
                }
                //emul.PlayFreq(freq);
            }
        });
        
        newpane.setOnTouchReleased(new EventHandler<TouchEvent>()
        {
            @Override
            public void handle(TouchEvent event)
            {
                
                //emul.StopFreq(freq);
            }
        });
        
        
        controllerNodes.add(newpane);
    }
    
    
    public void MicrotonalButton(String text, int x, int y, int w, int h)
    {
        Button newpane = new Button(text);
        anchorPane.getChildren().add(newpane);
        newpane.setLayoutX(x);
        newpane.setLayoutY(y);
        newpane.setPrefSize(w, h);

        if(microtonal)
        {
            newpane.setEffect(glowPressed);
        }
        else
        {
            newpane.setEffect(glow);
        }
                
        newpane.styleProperty().bind(stylePropertyDefault);
        newpane.toBack();

        newpane.setId("FrqBtn "+x+" "+y);
        
        newpane.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                microtonal = !microtonal;
                if(microtonal)
                {
                    newpane.setEffect(glowPressed);
                }
                else
                {
                    newpane.setEffect(glow);
                }
                //emul.PlayFreq(freq);
            }
        });
        
        newpane.setOnTouchReleased(new EventHandler<TouchEvent>()
        {
            @Override
            public void handle(TouchEvent event)
            {
                
                //emul.StopFreq(freq);
            }
        });
        
        
        controllerNodes.add(newpane);
    }
    
    
    
    public void SlideDownButton(String text, int x, int y, int w, int h, int totalheight, ArrayList<Node> ...allnodes)
    {
        Button newpane = new Button(text);
        anchorPane.getChildren().add(newpane);
        newpane.setLayoutX(x);
        newpane.setLayoutY(y);
        newpane.setPrefSize(w, h);

        newpane.setEffect(glow);
        newpane.styleProperty().bind(stylePropertyDefault);
        newpane.toBack();

        newpane.setId("SldDwnBtn "+x+" "+y);
        
        newpane.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event)
            {
                for (ArrayList<Node> nodes : allnodes)
                {
                    for (Node node : nodes)
                    {
                        double oldposy = node.layoutYProperty().get();
                        node.layoutYProperty().set(oldposy-totalheight);
                    }
                }
            }
        });
        
        
        controllerNodes.add(newpane);
    }    
    
    public void SlideUpButton(String text, int x, int y, int w, int h, int totalheight, ArrayList<Node> ...allnodes)
    {
        Button newpane = new Button(text);
        anchorPane.getChildren().add(newpane);
        newpane.setLayoutX(x);
        newpane.setLayoutY(y);
        newpane.setPrefSize(w, h);

        newpane.setEffect(glow);
        newpane.styleProperty().bind(stylePropertyDefault);
        newpane.toBack();

        newpane.setId("SldUpBtn "+x+" "+y);
        
        newpane.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event)
            {
                for (ArrayList<Node> nodes : allnodes)
                {
                    for (Node node : nodes)
                    {
                        double oldposy = node.layoutYProperty().get();
                        node.layoutYProperty().set(oldposy+totalheight);
                    }
                }
            }
        });
        
        
        controllerNodes.add(newpane);
    }
    
    
    
    
}

class TouchOnNode
{
    public int event_id;
    public Point2D point;
    public Node node;
    
    public TouchOnNode(int event_id_, double x, double y, Node node_)
    {
        event_id = event_id_;
        node = node_;
        point = new Point2D(x, y);
    }
}