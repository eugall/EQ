package com.heyaudiopro.eq;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {
    private MediaPlayer mediaPlayer;
    private Equalizer equalizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout eqLayout = findViewById(R.id.eq_layout);

        // Initialize standard Android media player
        // Note: You need an audio file named 'test_audio.mp3' in app/src/main/res/raw/
        mediaPlayer = MediaPlayer.create(this, R.raw.test_audio);
        
        // Tie the hardware Equalizer to this specific audio session
        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        equalizer.setEnabled(true);

        // Query the device's DAC for supported bands and limits
        short bands = equalizer.getNumberOfBands();
        final short minEQLevel = equalizer.getBandLevelRange()[0];
        short maxEQLevel = equalizer.getBandLevelRange()[1];

        // Dynamically build UI sliders based on supported hardware bands
        for (short i = 0; i < bands; i++) {
            final short bandIndex = i;
            
            TextView freqTextView = new TextView(this);
            // Convert milliHertz to Hertz
            freqTextView.setText((equalizer.getCenterFreq(bandIndex) / 1000) + " Hz");
            freqTextView.setTextColor(Color.parseColor("#555555"));
            eqLayout.addView(freqTextView);

            SeekBar seekBar = new SeekBar(this);
            seekBar.setMax(maxEQLevel - minEQLevel);
            // Center the slider at the current hardware default
            seekBar.setProgress(equalizer.getBandLevel(bandIndex) - minEQLevel);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Apply the new gain level to this specific frequency band
                    equalizer.setBandLevel(bandIndex, (short) (progress + minEQLevel));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            eqLayout.addView(seekBar);
        }
        
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Free up DSP resources when the app closes
        if (equalizer != null) equalizer.release();
        if (mediaPlayer != null) mediaPlayer.release();
    }
}

