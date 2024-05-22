import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Program {

    static Scanner scanner = new Scanner(System.in);
    static String url = "https://random-word-api.herokuapp.com/word?number=1";
    static String guessThisWord;
    static String dashesToString;
    static String consoleInput;
    static String playAgain;
    static String rstlne = "rstlne";
    static Character[] dashes;
    static Character charGuessed = ' ';
    static List<Character> charsGuessed = new ArrayList<>();
    static Boolean wordIsNotExitOrAbout = false;
    static Boolean breakpointReached = false;
    static Boolean userWon = false;
    static Boolean containsChar = false;
    static Integer incorrectGuessesLeft = 10;

    // Main method only handles startup and replay, no game logic is in this method
    public static void main(String[] args)
            throws InterruptedException {
        // Clearing the console here removes all content from the terminal and gives the program a cleaner experience
        clearConsole();
        System.out.println("Hangman Reworked is licensed under GNU GPL 3.0. \nThis program comes with ABSOLUTELY NO WARRANTY; for details type 'show w'. This is free software, and you are welcome to redistribute it under certain conditions; type 'show c' for details. \nFor more information about this software itself, type 'about'. \nDev note: Java takes a long time to grab the word from the API on my machine. (~8s on Java vs ~600ms on C#) Not sure if this is the case on others. Either way, the program isn't broken, it's just slow. Fuck Java. \nPress enter to continue.");
        scanner = new Scanner(System.in);
        consoleInput = scanner.nextLine();
        clearConsole();
        System.out.println("Welcome to hangman! \nPlease enter one character at a time, or the entire word. \nThere are no numbers or punctuation. \nYou have already been given the letters RSTLNE. \nTo exit, click the x button on the window, type \"exit\" into the console, or type CTRL + C at any time. \nGood luck, and have fun!");

        // Plays game, asks user if they would like to play again
        while (true) {
            playGame();
            System.out.println("Would you like to play again? (Y)es/(N)o");
            scanner = new Scanner(System.in);
            playAgain = scanner.nextLine();
            if (playAgain.equals("n") || playAgain.equals("no")) {
                System.out.println("Thanks for playing! \nCopyright (C) 2024 Christophe Thorpe. Licensed under GNU GPL v3.0.");
                TimeUnit.SECONDS.sleep(3);
                break;
            }

        }
    }

    // All the game logic is in this function
    public static void playGame() {
        // Ensure all variables are set to defaults
        guessThisWord = null;
        dashesToString = null;
        consoleInput = null;
        dashes = null;
        charGuessed = ' ';
        charsGuessed.clear();
        wordIsNotExitOrAbout = false;

        breakpointReached = false;
        userWon = false;
        containsChar = false;
        incorrectGuessesLeft = 10;

        // Get the word to guess from the API, repeating when invalid words are detected
        while (!wordIsNotExitOrAbout) {
            try {
                guessThisWord = HangClient.getWord(url);
                guessThisWord = guessThisWord.substring(2, guessThisWord.indexOf("\"]"));
                if (guessThisWord.equals("exit") || guessThisWord.equals("about")) {
                    continue;
                } else {
                    wordIsNotExitOrAbout = true;
                }
            } catch (IOException e) {
                System.out.println("Exception caught! Are you connected to the internet? \nDetails:\n");
                //noinspection ThrowablePrintedToSystemOut
                System.out.println(e);
                System.out.println("\nPress enter to exit.");
                scanner = new Scanner(System.in);
                System.exit(1);
            }

            // Create a new char array with length equal to the word
            dashes = new Character[guessThisWord.length()];
            for (int i = 0; i < guessThisWord.length(); i++) {
                dashes[i] = '-';
            }

            // Seek thru char array for RSTLNE
            for (int x = 0; x < guessThisWord.length(); x++) {
                for (int y = 0; y < rstlne.length(); y++) {
                    if (guessThisWord.charAt(x) == rstlne.charAt(y))
                        dashes[x] = rstlne.charAt(y);
                }
            }

            // Add RSTLNE to guessed chars list so user isn't penalized for repeat guesses
            charsGuessed.addAll(Arrays.asList(dashes));
            dashesToString = characterArrayToString(dashes);

            // Game will NOT run while this is true, so set it to false
            breakpointReached = false;

            // Game loop
            while (!breakpointReached) {
                containsChar = false;
                System.out.println(dashesToString);
                System.out.println(guessThisWord);
                System.out.println("Incorrect guesses left: " + incorrectGuessesLeft);

                // Console Input
                scanner = new Scanner(System.in);
                consoleInput = scanner.nextLine().toLowerCase();

                if (consoleInput.equals("show c")) {
                    // Show GNU GPL 3.0 Copyright/Distribution Information
                    System.out.println("This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. \nFor more information, visit https://www.gnu.org/licenses/. \n\nPress enter to continue.");
                    scanner = new Scanner(System.in);
                    consoleInput = scanner.nextLine();
                    continue;
                } else if (consoleInput.equals("show w")) {
                    // Show GNU GPL 3.0 Warranty Information
                    System.out.println("This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. \nFor more information, visit https://www.gnu.org/licenses/. \n\nPress enter to continue.");
                    scanner = new Scanner(System.in);
                    consoleInput = scanner.nextLine();
                    continue;
                } else if (consoleInput.equals(guessThisWord)) {
                    // User guesses entire word
                    userWon = true;
                    dashesToString = guessThisWord;
                    clearConsole();
                    break;
                } else if (consoleInput.equals("exit")) {
                    // User wants to exit the game
                    System.exit(0);
                } else if (consoleInput.equals("about")) {
                    // User wants to view the about page
                    clearConsole();
                    System.out.println("Hangman Reworked is a simple hangman game that runs in a console. \nThis program was originally written in C#, but this version is written in Java. It was programmed by Christopher Thorpe. \nThis program is free under certain conditions. These conditions are detailed in the GNU GPL v3.0. \nYou can also view this program's source code in your web browser at \"https://github.com/chris-thorpe3db/hangmanJava\". \n\nPress enter to continue.");
                    scanner = new Scanner(System.in);
                    consoleInput = scanner.nextLine();
                    clearConsole();
                } else if (consoleInput.length() != 1) {
                    // Console input is not a letter
                    clearConsole();
                    System.out.println("Please enter only one letter!");
                    continue;
                } else if (charsGuessed.contains(consoleInput.charAt(0))) {
                    // User already guessed this character
                    clearConsole();
                    System.out.println("You've already guessed that letter!");
                    continue;
                }

                // Parse string to char: allows us to compare individual characters in a string
                charGuessed = consoleInput.charAt(0);
                charsGuessed.add(consoleInput.charAt(0));

                // Compare guessed char to every char in the word; replace dashes as necessary
                for (int i = 0; i < guessThisWord.length(); i++) {
                    if (guessThisWord.charAt(i) == charGuessed) {
                        dashes[i] = charGuessed;
                        containsChar = true;
                    }
                }

                // Subtract guesses based on the value of containsChar
                if (!containsChar)
                    incorrectGuessesLeft--;

                // Convert char array to string to we can compare it easily
                dashesToString = characterArrayToString(dashes);

                //Check if the user has won or lost
                if (dashesToString.equals(guessThisWord)) {
                    userWon = true;
                    breakpointReached = true;
                } else if (incorrectGuessesLeft == 0) {
                    userWon = false;
                    breakpointReached = true;
                }

                clearConsole();
            }

            if (userWon) {
                System.out.println("Congratulations! The word was: " + guessThisWord + ". You had " + incorrectGuessesLeft + " guesses left.");
            } else {
                System.out.println("You lost! The word was:" + guessThisWord);
            }
            return; // For some reason the program will only work if this is here. I don't fucking know.
        }
    }

    private static String characterArrayToString(Character[] array) {
        StringBuilder sb = new StringBuilder();

        for (Character character : array) {
            sb.append(character);
        }
        return sb.toString();
    }

    // The program needs to clear the console multiple times, so making a quick 'n' dirty function for this makes it much easier to remember
    private static void clearConsole() {
        System.out.println(new String(new char[250]).replace("\0", "\r\n"));
    }

}


