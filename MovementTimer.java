/*
 * MovementTimer.java
 *
 * Copyright (C) [2023] [Susanne Coates]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * The MovementTimer class implements a timer with a user interface to remind users to take regular breaks.
 * It alternates between a work period and a break period, prompting the user to move or work at each interval.
 * The GUI displays the countdown timer and provides controls to start, stop, reset, and resume the timer.
 * The window remains always on top and the font size of the timer adjusts dynamically with the window size.
 * @author Susanne Coates (scoates@susannecoates.com)
 * @author Jennifer Zahnhiser (jaz-ai@susannecoates.com)
 */
public class MovementTimer extends JFrame {
  // Constants defining the duration of work and break periods in seconds
  private static final int WORK_TIME = 1800; // Duration of the work period
  private static final int BREAK_TIME = 300; // Duration of the break period

  // UI components for displaying time and controlling the timer state
  private JLabel timerDisplay; // Shows the remaining time
  private JButton startButton; // Starts the countdown
  private JButton stopButton; // Stops the countdown
  private JButton resetButton; // Resets the countdown to the initial value
  private JButton resumeButton; // Resumes the countdown for the next period

  // State management for the timer's current time and period (work/break)
  private int currentTime; // Tracks the current countdown time in seconds
  private boolean isWorkPeriod; // Indicates if the timer is in a work period
  private Timer timer; // Manages the countdown logic
  private Timer beepTimer; // Keeps track of the beeping timer

  /**
   * Constructs the MovementTimer by setting up the GUI components and 
   * initializing the timer logic.
   */
  public MovementTimer() {
    initializeGuiComponents();
    initializeTimerLogic();
  }

  /**
   * Sets up the GUI components and their layout within the frame.
   * It initializes a resizable display label for the timer and 
   * buttons for controlling the timer.
   * It also configures the frame to always stay on top and sets up the 
   * component listener for resizing events.
   */
  private void initializeGuiComponents() {
    // Set the title and default close operation
    setTitle("Movement Reminder Timer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Use GridBagLayout for flexibility in resizing
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    // Timer display label configuration
    timerDisplay = new JLabel("30:00");
    timerDisplay.setFont(new Font("Monospaced", Font.BOLD, 50));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.CENTER;
    add(timerDisplay, gbc);

    // Start button configuration
    startButton = new JButton("Start");
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    add(startButton, gbc);

    // Stop button configuration
    stopButton = new JButton("Stop");
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    add(stopButton, gbc);

    // Reset button configuration
    resetButton = new JButton("Reset");
    gbc.gridx = 0;
    gbc.gridy = 2;
    add(resetButton, gbc);

    // Resume button configuration
    resumeButton = new JButton("Switch");
    resumeButton.setEnabled(true); // Disabled by default
    gbc.gridx = 1;
    gbc.gridy = 2;
    add(resumeButton, gbc);

    // Add a component listener to the frame to handle resizing
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
          resizeTimerFont();
      }
    });

    // Adjust window size to fit all components
    pack();
    // Center window on screen
    setLocationRelativeTo(null);
    // Keep the timer on top
    setAlwaysOnTop(true);
  }

  /**
   * Function to resize timer font when window resized
   */
  private void resizeTimerFont() {
    // Get the current dimensions of the JFrame
    Dimension size = getSize();
    // Calculate the font size. You might need to tweak the divisor based on your preferences
    int fontSize = Math.max(size.width / 10, 50);
    // Set the new font size to the timer display
    timerDisplay.setFont(new Font("Monospaced", Font.BOLD, fontSize));
  }

  /**
   * Initializes timer logic and state management.
   */
  private void initializeTimerLogic() {
    // Set initial state
    isWorkPeriod = true;
    currentTime = WORK_TIME;

    // Initialize the timer
    timer = new Timer(1000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Decrement the current time
        currentTime--;
        // Update the display
        updateTimerDisplay();
        // Check if the timer has reached zero
        if (currentTime <= 0) {
          timer.stop();
          switchPeriods();
          resumeButton.setEnabled(true);
          playAlarmSound(); // play the alarm sound
        }
      }
    });

    // Configure action listeners for buttons
    // The stop and resume button action listeners stop the beep
    startButton.addActionListener(e -> {
      stopAlarmSound(); // Stop the beeping
      startTimer();
    });
    stopButton.addActionListener(e -> {
      stopAlarmSound(); // Stop the beeping
      stopTimer();
    });
    resetButton.addActionListener(e -> {
      stopAlarmSound(); // Stop the beeping
      resetTimer();
    });
    resumeButton.addActionListener(e -> {
      stopAlarmSound(); // Stop the beeping
      resumeTimer();
    });
  }

  /**
   * Updates the timer display label.
   */
  private void updateTimerDisplay() {
    int minutes = currentTime / 60;
    int seconds = currentTime % 60;
    timerDisplay.setText(String.format("%02d:%02d", minutes, seconds));
  }

  /**
   * Starts or resumes the timer.
   */
  private void startTimer() {
    timer.start();
    //resumeButton.setEnabled(false);
  }

  /**
   * Stops the timer.
   */
  private void stopTimer() {
    timer.stop();
    //resumeButton.setEnabled(true);
  }

  /**
   * Resets the timer to the initial value of the current period.
   */
  private void resetTimer() {
    currentTime = isWorkPeriod ? WORK_TIME : BREAK_TIME;
    updateTimerDisplay();
    //resumeButton.setEnabled(false);
  }

  /**
   * Switches between work and break periods.
   */
  private void switchPeriods() {
    isWorkPeriod = !isWorkPeriod;
    currentTime = isWorkPeriod ? WORK_TIME : BREAK_TIME;
    updateTimerDisplay();
  }

  /**
   * Resumes the timer for the next period.
   */
  private void resumeTimer() {
    switchPeriods();
    //startTimer();
  }

  /**
   * Method to start the alarm sound, which will beep every second.
   */
  private void playAlarmSound() {
    // If there's already a beeping timer running, stop it before creating a new one
    if (beepTimer != null && beepTimer.isRunning()) {
      beepTimer.stop();
    }
    
    // Initialize the beepTimer to beep every second
    beepTimer = new Timer(1000, e -> {
      try {
        Toolkit.getDefaultToolkit().beep(); // Play the default beep sound
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
    beepTimer.start();
  }


  /**
   * Method to stop the alarm sound.
   */
  private void stopAlarmSound() {
    if (beepTimer != null) {
      beepTimer.stop();
    }
  }

  /**
   * Entry point to start the timer application.
   * @param args Command line arguments (not used).
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MovementTimer frame = new MovementTimer();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
    });
  }
}
