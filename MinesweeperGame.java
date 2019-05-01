package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;

public class MinesweeperGame extends Game {          // главный класс игры
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];      // матрица для хранения информации о каждой ячейке
    private int countMinesOnField = 0;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;             // общее количество закрытых ячеек
    private int score;

    @Override
    public void initialize() {              // инициализируем игровое поле
        setScreenSize(SIDE, SIDE);          // устанавливаем размер игрового поля
        createGame();                       // создаем игру
    }

    private void createGame() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                int digit = getRandomNumber(10);                      // генератор чтобы 10% ячеек были минами
                if (digit == 0) {                                          // если сгенерированное число 0 то на этой ячейке мина
                    gameField[i][j] = new GameObject(j, i, true);   // заполняем матрицу новыми объектами(ячейками)
                    countMinesOnField++;
                } else {
                    gameField[i][j] = new GameObject(j, i, false);
                }
                setCellValue(i, j, "");                 // убираем все надписи в ячейках, нужно при рестарте
                setCellColor(i, j, Color.WHITESMOKE);         // устанавливаем цвет игрового поля
                countFlags = countMinesOnField;               // присваиваем количеству флагов количество мин
            }
        }
        countMineNeighbors();
    }

    private ArrayList<GameObject> getNeighbors(GameObject gameObject) {             // считаем количество заминированных соседей вокруг ячейки
        ArrayList<GameObject> neighbors = new ArrayList<>();                        // список для хранения заминированных ячеек
        for (int i = gameObject.y - 1; i <= gameObject.y + 1; i++) {
            for (int j = gameObject.x - 1; j <= gameObject.x + 1; j++) {
                if (i >= 0 && i < gameField.length && j >= 0 && j < gameField.length) {
                    neighbors.add(gameField[i][j]);
                }
            }
        }
        return neighbors;
    }

    private void countMineNeighbors() {                                         // добавляем количество заминированных вокруг ячеек в ячейку
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField.length; j++) {
                if (!gameField[i][j].isMine) {
                    ArrayList<GameObject> neighbors = getNeighbors(gameField[i][j]);
                    for (GameObject gameObject : neighbors) {
                        if (gameObject.isMine) {
                            gameField[i][j].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int a, int b) {
        if (gameField[b][a].isOpen != true && gameField[b][a].isFlag != true && isGameStopped != true) {   // если элемент уже открыт, отмечен флагом, игра остановлена ничего не делаем
            if (!gameField[b][a].isOpen) {
                countClosedTiles--;                                // уменьшаем счетчик открытых ячеек
                gameField[b][a].isOpen = true;
                if (gameField[b][a].isMine) {
                    setCellValueEx(a, b, Color.RED, MINE);         // если в ячейке мина рисуем мину устанавливаем цвет ячейки на красный
                    gameOver();
                } else {
                    if (gameField[b][a].countMineNeighbors == 0) {                  // если заминированных соседей нет
                        setCellColor(a, b, Color.GREEN);                            // цвет ячейки зеленый
                        setCellValue(a, b, "");                               // в ячейке ничего не пишем
                        for (GameObject o : getNeighbors(gameField[b][a])) {
                            openTile(o.x, o.y);                                     // рекурсивно вызываем мето опять
                        }
                    } else {
                        setCellColor(a, b, Color.AQUA);                             // если вокруг ячейки есть заминированный ячейки
                        setCellNumber(a, b, gameField[b][a].countMineNeighbors);    // пишем количество заминированных ячеек
                    }
                }
            }
        }
        if (gameField[b][a].isOpen && gameField[b][a].isMine != true) {             // увеличиваем счет если ячейка открыта и не мина на 5
            score = score + 5;
        }
        setScore(score);                                                                // устанавливаем счет
        if (countClosedTiles == countMinesOnField && gameField[b][a].isMine != true) {  // проверка на победу
            win();
            return;
        }
    }

    private void markTile(int x, int y) {
        if (gameField[y][x].isOpen) {                                            // если ячейка открыта ничего не делаем

        } else if (gameField[y][x].isFlag == false && countFlags == 0) {           // количество флагов 0 ничего не делаем

        } else if (isGameStopped) {

        } else if (gameField[y][x].isFlag == false) {                              // устанавливаем флаг
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else if (gameField[y][x].isFlag == true) {                               // возвращаем стандартный вид ячейке при снятии флага
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.WHITESMOKE);
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Game Over!", Color.RED, 55);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "You Win!", Color.YELLOW, 55);
    }

    private void restart() {                                // во всех ячейках ставим значения по умолчанию и запускаем игру заново
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {                                    // функция реагирующая на нажатие левой кнопки мыши
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {                                   // функция реагирующая на нажатие правой кнопки мыши
        markTile(x, y);
    }
}

