package com.javarush.games.minesweeper;

public class GameObject {       // класс ячеек игрового поля
    public int x;
    public int y;
    public boolean isMine;      // проверка есть ли мина в ячейке
    public int countMineNeighbors;      // количество заминированных соседних ячеек
    public boolean isOpen;              // показывает что ячейка открыта
    public boolean isFlag;

    GameObject(int x, int y, boolean isMine) {      // конструктор
        this.x = x;
        this.y = y;
        this.isMine = isMine;
    }
}
