package org;
import java.util.Scanner;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogRecord;

public class App {
    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final char PLAYER_SYMBOL = 'X';
    private static final char AI_SYMBOL = 'O';
    /*
    Код функціонує, але:
    - один метод виконує забагато функцій, читабельність досить низька
    - через те що все в одному методі, розширення в майбутньому ускладниться
    - є потенціал для помилок
    SonarQube плагін в IntelliJIdea аналізує код, та дуже змістовно дає поради для покращення архітектури
    */
    //System.out корисна, але у майбутньому (наприклад для дебагу) потрібно використовувати логування
    static {
        // Налаштування Logger
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord logRecord) {
                return logRecord.getMessage() + "\n";
            }
        });
        LOGGER.addHandler(handler);
    }
    //винесли функціональність за окремими класами з мейну
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        char[] boxes = initBoxes();

        LOGGER.info("Enter box number to select. Enjoy!");
        playGame(scan, boxes);
        scan.close();
    }

    private static char[] initBoxes() {
        return new char[]{ '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    }

    //основний луп гри
    private static void playGame(Scanner scan, char[] boxes) {
        int winner = 0;
        //SonarQube тут дає лінки де чіпають тему "чому break та continue треба використовувати менше"
        //якщо коротко, то проблема у читабельності та потенційних багах
        while (winner == 0) {
            printBoxes(boxes);
            // хід гравця, якщо гра ще не завершена
            if (!getUserMoveAndCheck(scan, boxes)) {
                return;
            }
            // перевірка на переможця після ходу гравця
            winner = getWinner(boxes);
            if (winner != 0) {
                printBoxes(boxes);
                printResult(winner);
                return;
            }
            // перевірка на нічию після ходу гравця
            if (!isBoxAvailable(boxes)) {
                printBoxes(boxes);
                printResult(3);
                return;
            }
            // хід ШІ - викликається тільки якщо є вільне місце на полі
            getAIMoveAndExecute(boxes);
            // перевірка переможця після ходу ШІ
            winner = getWinner(boxes);
            if (winner != 0) {
                printBoxes(boxes);
                printResult(winner);
                return;
            }
            // перевірка на нічию після ходу ШІ
            if (!isBoxAvailable(boxes)) {
                printBoxes(boxes);
                printResult(3);
                return;
            }
        }
    }

    //Друк ігрового поля
    private static void printBoxes(char[] boxes) {
        LOGGER.info("\n " + boxes[0] + " | " + boxes[1] + " | " + boxes[2] + " ");
        LOGGER.info("-----------");
        LOGGER.info(" " + boxes[3] + " | " + boxes[4] + " | " + boxes[5] + " ");
        LOGGER.info("-----------");
        LOGGER.info(" " + boxes[6] + " | " + boxes[7] + " | " + boxes[8] + " ");
    }
    /* Визначаємо переможця
     1 - переміг X(Гравець), 2 - переміг O(ШІ), 0 (гра продовжується)
    */
    private static int getWinner(char[] boxes) {
        int[][] winPatterns = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // рядки
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // колони
                {0, 4, 8}, {2, 4, 6}             // діагоналі
        };

        for (int[] pattern : winPatterns) {
            if (boxes[pattern[0]] == boxes[pattern[1]] &&
                    boxes[pattern[1]] == boxes[pattern[2]]) {
                if (boxes[pattern[0]] == PLAYER_SYMBOL) {
                    return 1;
                }
                if (boxes[pattern[0]] == AI_SYMBOL) {
                    return 2;
                }
            }
        }

        return 0;
    }

    private static boolean getUserMoveAndCheck(Scanner scan, char[] boxes) {
        while (true) {
            try {
                int input = scan.nextInt();

                if (input >= 1 && input <= 9) {
                    int index = input - 1;

                    if (boxes[index] != PLAYER_SYMBOL && boxes[index] != AI_SYMBOL) {
                        boxes[index] = PLAYER_SYMBOL;
                        return true;
                    }
                    LOGGER.warning("That one is already in use. Enter another.");
                } else {
                    LOGGER.warning("Invalid input. Enter again.");
                }
            } catch (Exception e) {
                LOGGER.warning("Invalid input. Enter again.");
                scan.nextLine();
            }
        }
    }
    //Отримання та виконання ходу ШІ
    //Math.random() замінено на java.util.Random.nextInt(), бо там рандом кращий

    private static void getAIMoveAndExecute(char[] boxes) {
        int rand = -1;

        while (rand == -1) {
            int candidate = RANDOM.nextInt(9);

            if (boxes[candidate] != PLAYER_SYMBOL && boxes[candidate] != AI_SYMBOL) {
                boxes[candidate] = AI_SYMBOL;
                rand = candidate;
            }
        }
    }

    private static boolean isBoxAvailable(char[] boxes) {
        for (char box : boxes) {
            if (box != PLAYER_SYMBOL && box != AI_SYMBOL) {
                return true;
            }
        }
        return false;
    }

    //виведення результату кінця гри
    private static void printResult(int winner) {
        switch (winner) {
            case 1:
                LOGGER.info("You won the game!\nCreated by Shreyas Saha. Thanks for playing!");
                break;
            case 2:
                LOGGER.info("You lost the game!\nCreated by Shreyas Saha. Thanks for playing!");
                break;
            case 3:
                LOGGER.info("It's a draw!\nCreated by Shreyas Saha. Thanks for playing!");
                break;
            default:
                break;
        }
    }
}