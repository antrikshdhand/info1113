#include <stdio.h>
#include <string.h>
#include <ctype.h> // isdigit()

#define NULL_SPACE 1
#define MAX_ARG1_LEN 8 // "history\0" (8 char)
#define MAX_ARG2_LEN 4 // longest move to store is "CRR\0" (4 char)
#define MAX_BUF 10 // longest valid input is "place CRR\0" (10 char)
#define BOARD_DIM 19 // 19x19 board
#define MAX_MOVES 361

// CONSTANTS USED FOR THE BOARD
#define EMPTY 5
#define MIST 9
enum player {
    BLACK = 0, 
    WHITE = 1
};

// GLOBAL
enum player g_currentPlayer;
int g_moveCounter;
int g_board[BOARD_DIM][BOARD_DIM];
char g_history[MAX_MOVES][MAX_ARG2_LEN];

/**
 * @brief Checks for 5 in a row horizontally, vertically, and diagonally,
 * given the last placed Gomoku piece.
 * 
 * @param column A character from A-S representing the column of the board.
 * @param row An integer from 1-19 representing the row of the board.
 * @return int Returns 0 on success, -1 on a tie, 1 otherwise.
 */
int checkWin(char column, int row)
{
    // Calculate numerical array indices from Gomoku board layout
    int row_i = BOARD_DIM - row;
    int col_j = column - 'A';

    int counter = 1;

    // Iterate over columns of tile's row to check for 5 in a row
    for (int j = 1; j < BOARD_DIM; j++)
    {
        if (g_board[row_i][j] == g_board[row_i][j - 1] 
            && (g_board[row_i][j] != EMPTY && g_board[row_i][j] != MIST))
        {
            counter++;
            if (counter == 5) {
                return 0;
            } 
            continue;
        }
       
        counter = 1;
    }

    counter = 1;

    // Iterate over rows of tile's column to check for 5 in a row
    for (int i = 1; i < BOARD_DIM; i++)
    {
        if ((g_board[i][col_j] == g_board[i - 1][col_j])
            && (g_board[i][col_j] != EMPTY && g_board[i][col_j] != MIST))
        {
            counter++;
            if (counter == 5) {
                return 0;
            } 
            continue;
        }

        counter = 1;
    }

    // Check all diagonals on the board (top left to bottom right)
    for (int i = 0; i < BOARD_DIM - 4; i++)
    {
        for (int j = 0; j < BOARD_DIM - 4; j++)
        {
            int val = g_board[i][j];
            if ((g_board[i + 1][j + 1] == val) && 
                (g_board[i + 2][j + 2] == val) &&
                (g_board[i + 3][j + 3] == val) &&
                (g_board[i + 4][j + 4] == val) &&
                (g_board[i][j] != EMPTY && g_board[i][j] != MIST))
            {
                return 0;
            }
        }
    }
    
    // Check all diagonals on the board (top right to bottom left)
    for (int i = 0; i < BOARD_DIM - 4; i++)
    {
        for (int j = 4; j < BOARD_DIM; j++)
        {
            int val = g_board[i][j];
            if ((g_board[i + 1][j - 1] == val) && 
                (g_board[i + 2][j - 2] == val) &&
                (g_board[i + 3][j - 3] == val) &&
                (g_board[i + 4][j - 4] == val) &&
                (g_board[i][j] != EMPTY && g_board[i][j] != MIST))
            {
                return 0;
            }
        }
    } 

    // Tie condition
    if (g_moveCounter == MAX_MOVES)
    {
        return -1;
    }

    return 1;
}

/**
 * @brief Adds a player's move to the game history.
 * 
 * @param placement The second argument of "place <C><R>".
 */
void addToHistory(char* placement)
{
    strcpy(g_history[g_moveCounter - 1], placement);

    return;
}

/**
 * @brief Increments the move counter by 1. Called after each successful
 * placement of a Gomoku counter.
 */
void incrementMoves(void)
{
    g_moveCounter++;

    return;
}

/**
 * @brief The function sets the starting player to Black and initialises the 
 * move counter to 0. It then sets the centre of the initial hole 
 * to the centre of the board i.e. J10. Called once at the start of each game.
 */
void initialiseGame(void)
{
    g_currentPlayer = BLACK;
    g_moveCounter = 0;

    // First initialise the entire array to empty
    for (int i = 0; i < BOARD_DIM; i++)
    {
        for (int j = 0; j < BOARD_DIM; j++)
        {
            g_board[i][j] = MIST;
        }
    }

    // Intialise centre 7x7 hole to mist
    for (int i = 6; i < BOARD_DIM - 6; i++)
    {
        for (int j = 6; j < BOARD_DIM - 6; j++)
        {
            g_board[i][j] = EMPTY;
        }
    }

    return;
}

/**
 * @brief Prints out all of the current game's moves so far.
 * Called when the command "history" is ran by the player.
 */
void printHistory(void)
{
    for (int i = 0; i < MAX_MOVES; i++)
    {
        printf("%s", g_history[i]);
    }
    printf("\n");

    return;
}

/**
 * @brief Return the current player as an integer.
 * 
 * @return int 0 is Black, 1 is White.
 */
int getCurrentPlayerInt(void)
{
    return g_currentPlayer;
}

/**
 * @brief Return the current player as a char.
 * 
 * @return char 'B' is Black, 'W' is White.
 */
char getCurrentPlayerChar(void)
{
    if (g_currentPlayer == BLACK)
    {
        return 'B';
    }
    else
    {
        return 'W';
    }
}

/**
 * @brief Alternates the current player. Called at the end of each successful
 * turn.
 */
void switchPlayer(void)
{
    g_currentPlayer = 1 - g_currentPlayer;

    return;
}

/**
 * @brief Places a Gomoku piece on the playing board given the column character 
 * and the row integer. The function places a '0' if the current player is
 * Black and places a '1' if the current player is White.
 * 
 * @param column A character from A-S representing the column of the board.
 * @param row An integer from 1-19 representing the row of the board.
 * @return int Returns -1 if the space is already occupied. Returns 0 on
 * success.
 */
int place(char column, int row)
{
    // Calculate numerical array indices from Gomoku board layout
    int row_i = BOARD_DIM - row;
    int col_j = column - 'A';
    
    // Check if coordinates are already occupied
    int entry = g_board[row_i][col_j];
    if (entry != EMPTY && entry != MIST)
    {
        return -1;
    }

    int who = getCurrentPlayerInt();
    if (who == 0)
    {
        g_board[row_i][col_j] = 0;
    }
    else if (who == 1)
    {
        g_board[row_i][col_j] = 1;
    }

    return 0;
}

/**
 * @brief Given an input and a starting index, the function extracts the first 
 * alphanumerical argument.
 * 
 * @param inputStr A character array of original user input to be parsed
 * @param argument A character array of size = MAX_ARG_LEN + 1
 * @param index Where the program will begin parsing inputStr from
 * @return int Returns -1 if argument contains non-alphanumerical characters or
 * if there is excessive whitespace around the argument. Returns 0 if no next
 * argument is detected. If next argument is detected, returns the start index
 * of the next argument.
 */
int getArgument(char* inputStr, char* argument, int index)
{
    int nextArgPresent = 0;
    int nextArgPosition = 0;
    for (size_t i = 0; i < sizeof(argument); i++)
    {
        if ((inputStr[i + index] >= 'a' && inputStr[i + index] <= 'z') 
            || (inputStr[i + index] >= 'A' && inputStr[i + index] <= 'Z') 
            || (inputStr[i + index] >= '0' && inputStr[i + index] <= '9'))
        {
            argument[i] = inputStr[i + index];
        }
        else
        {
            // essentially stripping the newline character
            if (inputStr[i + index] == '\n')
            {
                argument[i] = '\0';
                break;
            }
            else if (inputStr[i + index] == ' ')
            {
                // Check for excessive whitespace
                if ((i + index + 1) == strlen(inputStr) || 
                    inputStr[i + index + 1] == '\n')
                {
                    return -1;
                }
                
                argument[i] = '\0';
                nextArgPresent = 1;
                nextArgPosition = i + index + 1;
                break;
            }
            else
            {
                return -1; // some non-alphanumerical input e.g. histo&&
            }
        }
    }
    
    if (nextArgPresent)
    {
        return nextArgPosition;
    }
    
    return 0;
}

/**
 * @brief Calls getArgument() function to detect 1st and 2nd arguments 
 * of userInput.
 * 
 * @param inputStr A character array of original user input to be parsed
 * @param firstArgument A character array of size = MAX_ARG1_LEN + 1
 * @param secondArgument A character array of size = MAX_ARG2_LEN + 1
 * @return int Returns -1 on error in parsing or if 3 or more arguments
 * have been detected. Returns 0 on success.
 */
int strTokenise(char* inputStr, char* firstArgument, char* secondArgument)
{
    if (inputStr[0] < 'a' || inputStr[0] > 'z') 
    {
        return -1;
    }
    
    int result1 = getArgument(inputStr, firstArgument, 0);
    if (result1 == 0)
    {
        // Do nothing. Only one argument has been detected in the inputStr
        // and this has been parsed into firstArgument[]
    }
    else if (result1 == -1)
    {
        // An error has been found while parsing the firstArgument
        // e.g. the presence of non-alphanumerical characters e.g. histor%$#@
        return -1;
    }
    else {
        // A second argument has been detected at index = return value.
        int result2 = getArgument(inputStr, secondArgument, result1);
        if (result2 == 0) 
        {
            // Do nothing
        }
        else
        {
            // As we are only looking for 2 arguments, any detection of
            // another argument is an error.
            return -1;
        }
    }

    return 0;
}

/**
 * @brief Uses fgets() to get user input while checking for buffer overflow.
 * If buffer overflow has been detected, the function clears the input buffer
 * while checking for the presence of a third argument. 
 * If there is a third argument present, error should be "Invalid!". 
 * If simply buffer overflow, error could either be "Invalid!" or
 * "Invalid coordinate!".
 * 
 * @param str Char array to store user input into
 * @param size Size of str
 * @return int Returns -1 if only buffer overflow has occurred. Returns -2
 * if buffer overflow has occurred AND the presence of a third argument
 * has been detected. Returns 0 on success. Returns 1 on EOF.
 */
int getUserInput(char* str, size_t size)
{
    char* fgetsResult = fgets(str, size, stdin);

    if(feof(stdin))
    {
        return 0;
    }

    // We need to check for buffer overflow by checking if a \n character 
    // has been read or not
    int spaceCounter = 0;
    if (fgetsResult != NULL)
    {
        int newLineRead = 0;
        for (size_t i = 0; i < size; i++)
        {
            if (str[i] == '\n')
            {
                newLineRead = 1;
            }
            else if (str[i] == ' ')
            {
                spaceCounter++;
            }
        }

        // Buffer overflow occurred
        if (newLineRead == 0)
        {
            // Clear input buffer
            int c;
            while ((c = getchar()) != '\n' && c != EOF) { 
                if (c == ' ')
                {
                    spaceCounter++;
                }
            }

            if (spaceCounter > 1)
            {
                return -2; // more than 2 arguments = "Invalid!"
            }
            else
            {
                return -1; // could be "Invalid!" or "Invalid coordinate"
            }
        }
    }
    else
    {
        return 1;
    }

    return 0;
}

/**
 * @brief Given an array and its size, the function clears the array by
 * setting all of its values to the null character '/0'.
 * 
 * @param arr An array to be cleared
 * @param size The size of arr
 */
void clearArray(char* arr, int size)
{
    for (int i = 0; i < size; i++)
    {
        arr[i] = '\0';
    }

    return;
}

/**
 * @brief Given Gomoku column and row coordinates, the function calculates 
 * the position of the centre of the next "mist hole" as array indice values.
 * It stores the i and j indices in ijArray[0] and ijArray[1] respectively.
 * 
 * @param column The Gomoku column coordinate [A-S]
 * @param row The Gomoku row coordinate [1-19]
 * @param ijArray An int array of size 2 which will store the i and j
 * indices of the centre of the next mist hole.
 */
void calculateMist(char column, int row, int* ijArray)
{
    // Calculate array indices from Gomoku board layout
    // int row_i = BOARD_DIM - row;
    int col_j = column - 'A';

    // Numerical coordinate system
    int x_num = col_j + 1;
    int y_num = row;

    // Centre of mist hole (numerical)
    int x_mist = 1 + (5 * (x_num * x_num) + 3 * x_num + 4) % 19;
    int y_mist = 1 + (4 * (y_num * y_num) + 2 * y_num - 4) % 19;

    // Centre of mist hole (i, j)
    int mist_j = x_mist - 1;
    int mist_i = y_mist;
    
    ijArray[0] = mist_i;
    ijArray[1] = mist_j;

    return;
}

/**
 * @brief Prints out the view command for the current game state. First string
 * represents the centre coordinate of the 7x7 mist hole, and the second string
 * denotes the status of each point in the hole.
 * #: black
 * o: white
 * .: empty
 * x: off-board
 */
void view()
{    
    if (g_moveCounter == 0)
    {
        printf("J10,.................................................\n");
        return;
    }

    // Call calculateMist() function on the latest move
    char* lastMove = g_history[g_moveCounter - 1];
    char column;
    int row;

    column = lastMove[0];
    if (strlen(lastMove) == 2) 
    {
        row = lastMove[1] - '0';
    } 
    else
    {
        row = (lastMove[1] - '0') * 10 + (lastMove[2] - '0');
    }

    int centre[2] = {0, 0};
    calculateMist(column, row, centre);

    // Centre of mist hole (array indices)
    int mist_i = BOARD_DIM - centre[0];
    int mist_j = centre[1];
 
    // Centre of mist hole (Go style)
    char mist_col = mist_j + 'A';
    int mist_row = BOARD_DIM - mist_i;

    /* PRINT THE VIEW COMMAND OUTPUT */

    printf("%c%d,", mist_col, mist_row);
    
    for (int i = -3; i <= 3; i++)
    {
        if ((mist_i + i) < 0 || (mist_i + i) >= 19) 
        {
            printf("xxxxxxx");
        }
        else
        {
            for (int j = -3; j <= 3; j++)
            {
                if ((mist_j + j) < 0 || (mist_j + j) >= 19)
                {
                    printf("x");
                }
                else
                {
                    if (g_board[mist_i + i][mist_j + j] == BLACK)
                    {
                        printf("#");
                    }
                    else if (g_board[mist_i + i][mist_j + j] == WHITE) 
                    {
                        printf("o");
                    }
                    else if (g_board[mist_i + i][mist_j + j] == EMPTY) 
                    {
                        printf(".");
                    }
                    else if (g_board[mist_i + i][mist_j + j] == MIST) 
                    {
                        printf(".");
                    }
                }
            }
        }
    }
    printf("\n");
}

int main(void)
{
    initialiseGame();

    char firstArgument[MAX_ARG1_LEN];
    char secondArgument[MAX_ARG2_LEN];
    char userInput[MAX_BUF + 1]; // +1 to account for \n

    while (1) 
    {   
        clearArray(userInput, sizeof(userInput));
        clearArray(firstArgument, sizeof(firstArgument));
        clearArray(secondArgument, sizeof(secondArgument));
        
        // Get user input
        int userInputResult = getUserInput(userInput, sizeof(userInput));
        if (userInputResult == -2) // more than 2 arguments detected
        {
            printf("Invalid!\n");
            continue;
        }
        else if (userInputResult == -1) // buffer overflow
        {
            // If buffer overflow occurs with 'place' as first command
            // error message should be "Invalid coordinate"
            if (strncmp(userInput, "place ", 6) == 0)
            {
                printf("Invalid coordinate\n");
                continue;
            }
            // Else it is just simple buffer overflow
            else
            {
                printf("Invalid!\n");
                continue;
            }
        }
        else if (userInputResult == 1)
        {
            // EOF reached
            break;
        }

        // Split up user input into two arguments
        int tokenise = strTokenise(userInput, firstArgument, secondArgument);
        if (tokenise == -1) // error in parsing or more than 3 args detected
        {
            printf("Invalid!\n");
            continue;
        }

        /***************************** BEGIN GAME *****************************/
        
        /* place command */
        if (strcmp(firstArgument, "place") == 0)
        {
            // Check for lack of a second argument
            if (strlen(secondArgument) == 0)
            {
                printf("Invalid!\n");
                continue;
            }

            // Check length of second argument in case user inputs a 
            // 1 character parameter e.g. "place D" or user enters a long 
            // parameter which is valid for first 3 char e.g. "place D122222"
            if (strlen(secondArgument) != 2 && strlen(secondArgument) != 3)
            {
                printf("Invalid coordinate\n");
                continue;
            }
            
            // Getting row and column coordinates
            char column;
            int row;

            column = secondArgument[0];
            // error-check column
            if (column < 'A' || column > 'S')
            {
                printf("Invalid coordinate\n");
                continue;
            }

            // all rows have at least one digit [1, 9]
            row = secondArgument[1] - '0';
            if (row < 1 || row > 9) 
            {
                printf("Invalid coordinate\n");
                continue;
            }

            // if row number > 9, need to account for the third digit
            if (strlen(secondArgument) == 3)
            {
                if (isdigit(secondArgument[2]) == 0) // third digit isn't [0, 9]
                {
                    printf("Invalid coordinate\n");
                    continue;
                }
                int temp = row * 10 + (secondArgument[2] - '0');
                
                // final check
                if (temp < 1 || temp > 19)
                {
                    printf("Invalid coordinate\n");
                    continue;
                }
                row = temp;
            }

            // Placing a game piece
            int success = place(column, row);
            if (success == -1)
            {
                printf("Occupied coordinate\n");
                continue;
            }

            // On successful placement:
            // - increment moves counter by 1
            incrementMoves();

            // - add placement to history
            addToHistory(secondArgument);

            // - check for win condition
            int checkWinResult = checkWin(column, row);
            if (checkWinResult == 0)
            {
                if (g_currentPlayer == 0) printf("Black wins!\n");
                else printf("White wins!\n");
                printHistory();
                printf("Thank you for playing!\n");
                break;
            }
            else if (checkWinResult == -1)
            {
                printf("Wow, a tie!\n");
                printHistory();
                printf("Thank you for playing!\n");
                break;
            }
            
            // - switch player
            switchPlayer();

            // Turn is over
            continue;
        }
        
        /* REST OF THE GAME FEATURES */

        // Any other commands only require 1 argument
        if (strlen(secondArgument) != 0)
        {
            printf("Invalid!\n");
        }
            
        /* who command */
        else if (strcmp(firstArgument, "who") == 0)
        {
            printf("%c\n", getCurrentPlayerChar());
        }

        /* term command */
        else if (strcmp(firstArgument, "term") == 0)
        {
            return 1;
        }

        /* resign command */
        else if (strcmp(firstArgument, "resign") == 0)
        {
            if (g_currentPlayer == 0) printf("White wins!\n");
            else printf("Black wins!\n");

            printHistory();
            printf("Thank you for playing!\n");
            break;
        }

        /* view command */
        else if (strcmp(firstArgument, "view") == 0)
        {
            view();
        }

        /* history command */
        else if (strcmp(firstArgument, "history") == 0)
        {
            printHistory();
        }

        /* any other invalid input */
        else
        {
            printf("Invalid!\n");
        }
    }

    return 0;
}
