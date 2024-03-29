package com.nitk.it.glassassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.io.File;
import java.util.Locale;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;

    /**
     * "Hello World!" {@link View} generated by {@link #buildView()}.
     */
    private View mView;

    private GestureDetector mGestureDetector;

    private static final int TAKE_PICTURE_REQUEST = 1;

    private static final int GENERATE_CAPTION_SOCKET_REQUEST = 2;

    private static final int CAPTION_RESULT_REQUEST = 3;

    private static final int OBJECT_DETECTION_REQUEST = 4;

    private File imageFile;

    private String platform;

    private String caption;

    private String objects;

    private static final String CAPTURE_IMAGE = "Please tap to continue";

    private TextToSpeech textToSpeech;

    private String DETECT_OBJECTS = "Double tap to detect the various objects in the scene, else swipe down to exit";

    @Override
    protected void onCreate(Bundle bundle) {
        System.out.println("Entering MainActivity OnCreate");
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        mView = buildView();

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });

        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOptionsMenu();
            }
        });

        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);

        // To be removed
        platform = "Sockets";
        takePicture();
    }

    private GestureDetector createGestureDetector(final Context context) {
        System.out.println("Entering MainActivity createGestureDetector");
        GestureDetector gestureDetector = new GestureDetector(context);

        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN) {
                    finish();
                }
                return false;
            }
        });

        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int i, int i1) {

            }
        });

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float v, float v1, float v2) {
                return false;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        System.out.println("Entering MainActivity OnGenericMotionEvent");
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
// To be uncommented

/*    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        System.out.println("Entering MainActivity onCreatePanelMenu");
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }*/

/*    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        System.out.println("Entering onMenuItemSelected");
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case R.id.connect_socket:
                    platform = "Sockets";
                    break;
                case R.id.connect_bluetooth:
                    platform = "Bluetooth";
                    break;
                case R.id.connect_raspPi:
                    platform = "RaspberryPi";
                    break;
            }
            takePicture();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }*/

    private void connect(Boolean detectObjects) {
        System.out.println("Entering MainActivity connect");
        if (platform.equalsIgnoreCase("Sockets")){
            Intent intent = new Intent(getBaseContext(), SocketClientActivity.class);
            intent.putExtra("IMAGE", imageFile);
            intent.putExtra("DETECTOBJECTS", detectObjects);
            if (!detectObjects) {
                startActivityForResult(intent, GENERATE_CAPTION_SOCKET_REQUEST);
            } else {
                startActivityForResult(intent, OBJECT_DETECTION_REQUEST);
            }
        }
        else if(platform.equalsIgnoreCase("Bluetooth")){

        }
        else if(platform.equalsIgnoreCase("RaspberryPi")){

        }
        else{
            // TODO: throw invalid param exception
        }
    }

    private void detectObjects() {
        connect(true);
//        textToSpeech.speak(DETECT_OBJECTS, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void takePicture() {
        System.out.println("Entering MainActivity takePicture");

        // Adding textToSpeech for capturing image
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    System.out.println("Entering MainActivity onActivityResult.TextToSpeech.onInit");
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.speak(CAPTURE_IMAGE, TextToSpeech.QUEUE_FLUSH, null);
                    generateAudioSignal();
                }
            }
        });
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    private void generateAudioSignal() {
        System.out.println("Entering MainActivity generateAudioSignal");
        textToSpeech.speak(CAPTURE_IMAGE, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Entering MainActivity onActivityResult");
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            System.out.println("         MainActivity onActivityResult PictureIntent");
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            processPictureWhenReady(picturePath);
            // TODO: Show the thumbnail to the user while the full picture is being processed.
        }
        else if(requestCode == GENERATE_CAPTION_SOCKET_REQUEST  && resultCode == RESULT_OK) {
            System.out.println("         MainActivity onActivityResult SocketClientActivity intent");
            caption = data.getStringExtra("CAPTION");
            System.out.println("         MainActivity caption got from SocketClientActivity: " + caption);
            convertToAudio();
        } else if (requestCode == CAPTION_RESULT_REQUEST && resultCode == RESULT_OK) {
            System.out.println("         MainActivity onActivityResult CaptionActivity intent");
            detectObjects();
        } else if (requestCode == OBJECT_DETECTION_REQUEST && resultCode == RESULT_OK) {
            System.out.println("         MainActivity onActivityResult SocketClientActivity intent");
            objects = data.getStringExtra("OBJECTS");
            System.out.println("         MainActivity objects got from SocketClientActivity: " + caption);
            textToSpeech.speak(objects, TextToSpeech.QUEUE_FLUSH, null);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void convertToAudio() {
        Intent captionIntent = new Intent(getBaseContext(), CaptionActivity.class);
        captionIntent.putExtra("IMAGE", imageFile);
        captionIntent.putExtra("CAPTION", caption);
        startActivityForResult(captionIntent, CAPTION_RESULT_REQUEST);
    }

    private void processPictureWhenReady(final String picturePath) {
        System.out.println("Entering MainActivity processPictureWhenReady");
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            // The picture is ready; process it.
            System.out.println("        MainActivity: pictureFile exists");
            imageFile = pictureFile;
            connect(false);
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            // TODO: Show the thumbnail and a progress indicator to the user while the full picture is being processed.

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    /**
     * Builds a Glass styled "Describe Scene!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        System.out.println("Entering MainActivity buildView");
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.COLUMNS);

        card.setText(R.string.voice_command);
        card.addImage(R.drawable.glass_logo);
        return card.getView();
    }

}
