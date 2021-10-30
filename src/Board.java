import java.util.HashSet;
import java.util.Set;

public class Board {
    private int[][] blocks;
    private int zeroX;
    private int zeroY;
    private int h;
    public static int spaceLine = 4;
    public static int spacePos = 15;
    public static int size = 16;
    public static int[] field = new int[size];
    public static String strLine;
    public boolean left = false;
    public boolean right = false;
    public boolean top = false;
    public boolean bottom = false;

    public Board(int[][] blocks) {
        int[][] blocks2 = deepCopy(blocks);
        this.blocks = blocks2;

        h = manhattanDistance();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
//                if (blocks[i][j] != (i*dimension() + j + 1) && blocks[i][j] != 0) {  // если 0 не на своем месте - не считается
//                    h += 1;
//                }
                if (blocks[i][j] == 0) {
                    zeroX = (int) i;
                    zeroY = (int) j;
                }
            }
        }
    }

    private static void initField(String str) {
        for (int i = 0; i < size; i++) {
            int num = str.charAt(i) - '0';
            field[i] =  num < 10 ? num : num - 7;
            if (num == 0) {
                spacePos = i;
                if (spacePos < 4)
                    spaceLine = 1;
                else if (spacePos < 8)
                    spaceLine = 2;
                else if (spacePos < 12)
                    spaceLine = 3;
                else spaceLine = 4;
            }
        }
    }

    public int manhattanDistance() {
        int manhattan = 0;

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] != (i*4 + j + 1) && blocks[i][j] != 0) {  // если 0 не на своем месте - не считается
                    int pos = blocks[i][j] - 1;
                    int x2 = pos / 4;
                    int y2 = pos % 4;
                    manhattan += (Math.abs(i - x2) + Math.abs(j - y2));
                }
            }
        }

        return manhattan;
    }

    public Board(String str) {
        strLine = str;
        blocks = Solver.initArrayFromString(strLine);
        this.blocks = deepCopy(blocks);

        h = 0;
        for (int i = 0; i < blocks.length; i++) {  //  в этом цикле определяем координаты нуля и вычисляем h(x)
            for (int j = 0; j < blocks[i].length; j++) {
                
                h = manhattanDistance();
                
                if (blocks[i][j] == 0) {
                    zeroX = (int) i;
                    zeroY = (int) j;
                }
            }
        }
    }

    private static int getPosByNumber(int num)
    {
        int pos = 0;

        for (int i = 0; i < size; i++)
            if (field[i] == num)
            {
                pos = i;
                break;
            }
        return pos;
    }

    private static int getLessNumbersCount(int number)
    {
        int count = 0;
        int pos = getPosByNumber(number);
        for (int i = pos + 1; i < size; i++)
            if (field[i] < number) count++;
        return pos > spacePos ? count : --count;
    }

    public static boolean isValid() {
        initField(strLine);
        int sum = 0;
        for (int i = 1; i < size; i++)
            sum += getLessNumbersCount(i);
        sum += spaceLine;;
        return sum % 2 == 0;
    }

    public int dimension() {
        return blocks.length;
    }

    public int h() {
        h = manhattanDistance();
        return h;
    }

    public boolean isGoal() {
        return h == 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        if (board.dimension() != dimension()) return false;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] != board.blocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        int size = dimension();
        for(int i = 0; i<size; i++) {
            for(int j = 0; j<size; j++) {
                hash = (hash<<2)^blocks[i][j];
            }
        }
        return hash;
    }

    public Iterable<Board> neighbors() {
        Set<Board> boardList = new HashSet<Board>();

        // получаем новое состояние + запоминаем, откуда пришли

        Board newBoard = chng(getNewBlock(), zeroX, zeroY, zeroX, zeroY + 1);
//        newBoard.top = true;
        boardList.add(newBoard);

        newBoard = chng(getNewBlock(), zeroX, zeroY, zeroX, zeroY - 1);
//        newBoard.top = false;
//        newBoard.bottom = true;
        boardList.add(newBoard);

        newBoard = chng(getNewBlock(), zeroX, zeroY, zeroX - 1, zeroY);
//        newBoard.bottom = false;
//        newBoard.left = true;
        boardList.add(newBoard);

        newBoard = chng(getNewBlock(), zeroX, zeroY, zeroX + 1, zeroY);
//        newBoard.left = false;
//        newBoard.right = true;
        boardList.add(newBoard);

        return boardList;
    }

    private int[][] getNewBlock() { //  опять же, для неизменяемости
        return deepCopy(blocks);
    }

    private Board chng(int[][] blocks2, int x1, int y1, int x2, int y2) {  //  в этом методе меняем два соседних поля

        if (x2 > -1 && x2 < dimension() && y2 > -1 && y2 < dimension()) {
            int t = blocks2[x2][y2];
            blocks2[x2][y2] = blocks2[x1][y1];
            blocks2[x1][y1] = t;
            return new Board(blocks2);
        } else
            return null;

    }


    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                s.append(String.format("%2d ", blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    private static int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }

        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = new int[original[i].length];
            for (int j = 0; j < original[i].length; j++) {
                result[i][j] = original[i][j];
            }
        }
        return result;
    }
}