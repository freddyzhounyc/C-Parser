/**
 * The BlockTracer Class handles the parsing of the C file and implements a stack to be able to hold each block of code.
 *
 * @author Freddy Zhou
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;
import java.util.Scanner;

public class BlockTracer {
    /**
     * Handles the
     * @param args - Not used
     * @throws FileNotFoundException
     *  Handles the case where if the file asked to be found does not exist.
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner cin = new Scanner(System.in); // Gets the user input.
        int lineCount = 0;

        // prompts the user to select the C file to parse.
        System.out.print("Enter C program filename: ");
        File file = new File(cin.nextLine()); // Creates a file and finds it.
        Scanner fin = new Scanner(file);
        System.out.println();

        Stack<Block> programStack = new Stack<>(); // Creates a new stack for the program to keep track of the blocks.

        // Gets past the initial declarations like "#include <iostream>"
        String data = "";
        data = fin.nextLine();
        lineCount++;
        while (!data.substring(0, 1).equals("{")) {
            data = fin.nextLine();
            lineCount++;
        }
        lineCount++;

        int blockCounter = 0;
        // Actual run through of the parsing itself.
        do {
            // Adds a new block to the program stack if the code is written in a single line.
            if (data.contains("{ ")) {
                Block newBlock = new Block(); // Makes a new block to be added to the program stack later on.
                programStack.add(newBlock);
                blockCounter++;

                int lineIndexCounter = 0;

                // Iterates until it the line reaches the keyword/key symbol "}"
                while (!data.substring(lineIndexCounter + 2, lineIndexCounter + 3).equals("}")) {
                    checkLineForIntKeywordNoSpaces(data, newBlock); // calls the methods that specifically handles when code is called on one line.

                    // Checks for whether there is a print statement along with checking for int keyword.
                    if (!data.substring(lineIndexCounter + 2, lineIndexCounter + 3).equals(";")){
                        if (data.substring(lineIndexCounter, lineIndexCounter + 3).equals("/*$")){
                            printLocalKeyword(programStack.peek());
                        }
                    }
                    lineIndexCounter++; // Iterates through the while loop.
                }
                programStack.pop(); // Pops off the top of the program stack once out of the while loop/
                data = fin.nextLine(); // Finds the next line and reassigns it to data.
                lineCount++;
            }
            // Checks if the line contains only an open curly bracket, indicating the creation of a new block.
            if (data.contains("{")) {
                Block newBlock = new Block();
                programStack.add(newBlock);
                blockCounter++;

                data = fin.nextLine();
                lineCount++;

                // If there is not an opening curly bracket, the parser will check for a closed curly bracket, indicating the end of a block.
            } else if (data.contains("}")) {
                blockCounter--;
                programStack.pop(); // Pops off the top block due to the block ending.

                // Checks if the program stack is empty before moving onto a possible next line.
                if (!programStack.isEmpty()){
                    data = fin.nextLine();
                    lineCount++;
                }
                // If neither an opening or closing curly bracket, the parser will check for a closing parenthesis in case we are on a line that is
                // an if statement, while loop, or others where the opening curly bracket is on the next line.
            } else if (data.contains(")")) {
                if (!programStack.isEmpty()) {
                    data = fin.nextLine();
                    lineCount++;
                }
                // If it doesn't detect any keyword that requires pushing or popping with the stack, the parser will check for print and/or int keywords.
            } else {
                int lineStringIndexCounter = 0;
                // Determintes whether the line contains a print statement.
                boolean printStatement = false;
                if (data.contains("/*$print"))
                    printStatement = true;

                // If the parser detects the print keyword if till go into checking whether it wants the local variables printed or specific variables.
                if (printStatement) {
                    // Will call the print local variables method if the line contains "/*$print LOCAL*/"
                    if (data.contains("/*$print LOCAL*/")) {
                        printLocalKeyword(programStack.peek()); // Calls another method.
                        // Takes place if there is no print local variable keyword, but it is still a print statement. Thus meaning printing a specific variable.
                    } else {
                        // Iterates the cursor until it hits the t letter in "print."
                        while (!data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals("t")) { // 11
                            lineStringIndexCounter++;
                        }
                        lineStringIndexCounter += 2; // Moves cursor forwards towards the start of the variable.
                        String varName = "";
                        int varLength = 0;
                        // Finds the variable length to be used later on.
                        while (!data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals("*")) {
                            varLength++;
                            lineStringIndexCounter++;
                        }
                        // Moves cursor back to the beginning of the variable name.
                        lineStringIndexCounter -= varLength;
                        varName = data.substring(lineStringIndexCounter, lineStringIndexCounter + varLength); // Finds variable name.
                        printVariableKeyword(varName, programStack); // Calls the print statement for the print variable keyword for that specific variable.
                    }
                } else {
                    checkLineForIntKeyword(data, programStack.peek()); // If it's not a print statement in the line, it will check the line for the int keyword.
                }

                data = fin.nextLine(); // Moves onto the next line to be read in for the parser.
                lineCount++;
            }
        } while (!programStack.isEmpty()); // Condition for the do-while loop.
    }

    /**
     * Checks the specific line that is passed in the parameter in that specific block for the int keyword with no spaces between the possible assignment operator.
     * @param data
     *  The line that will be passed into the parser.
     * @param newBlock
     *  The block that the line currently being assessed is in.
     * @custom.precondition
     *  Data should be a String that contains a line in the C program passed in the file.
     *  NewBlock should be a block that is referring to the current block in the program stack.
     * @custom.postcondition
     *  Variables that are paired with the int keyword will be added to the variables linked list in the program stack block that is passed in.
     */
    public static void checkLineForIntKeywordNoSpaces(String data, Block newBlock){
        int lineStringIndexCounter = 0;

        // checks if the current line is a multi declaration line
        boolean multiDeclaration = false;
        while (!data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(";")) {
            if (data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(","))
                multiDeclaration = true;
            lineStringIndexCounter++;
        }
        lineStringIndexCounter = 0;

        // checks for the int keyword if the line is NOT a multi declaration line
        if (!multiDeclaration) {
            // while loop checks to see if we are at the end of the line accounting for only "int " (iterates through the line until it finds a semicolon)
            while (!(data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(";")) &&
                    !(data.substring(lineStringIndexCounter + 3, lineStringIndexCounter + 4).equals(";"))){
                // if found the int keyword then create an instance of the variables class and add it to the list in the block
                if (data.substring(lineStringIndexCounter, lineStringIndexCounter + 4).equals("int ")) { // lsic = 6
                    // finds how long the variable being declared is
                    int varLength = 0;
                    int tempLineStringIndexCounter = lineStringIndexCounter + 4; // 10
                    while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals("=") &&
                            !data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(";")){
                        varLength++;
                        tempLineStringIndexCounter++; // 11
                    }
                    // finds how long the integer value is
                    int intLength = 0;
                    tempLineStringIndexCounter = varLength + 5 + lineStringIndexCounter; // 12
                    while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(" ") &&
                            !data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(";")) {
                        intLength++;
                        tempLineStringIndexCounter++; // 13
                    }

                    // checks if there is an assignment operator or not
                    if (!data.substring(lineStringIndexCounter + varLength + 2, lineStringIndexCounter + varLength + 3).equals(";")){
                        // A try catch is used to determine if there is a number after the assignment operator if we are at the end of a line.
                        try {
                            // creates an instance of the variable class with the specified value after the int keyword.
                            Variable newVar = new Variable(data.substring(lineStringIndexCounter + 4, lineStringIndexCounter + varLength + 4),
                                    Integer.parseInt(data.substring(lineStringIndexCounter + varLength + 5, lineStringIndexCounter + varLength + 5 + intLength)));
                            // if a variable is already present in the block
                            boolean varAlreadyPresent = false;
                            int index = 0;
                            // Goes through each of the variables in the block to check if the variable is already present.
                            for (int i = 0; i < newBlock.getVariables().size(); i++){
                                if (newBlock.getVariables().get(i).getName().equals(newVar.getName())){
                                    index = i; // records the index the variable is at.
                                    varAlreadyPresent = true;
                                }
                            }
                            // If the variable is already present in the linked list of the current block, it will replace the old variable with the updated
                            // variable at the saved index location.
                            if (varAlreadyPresent)
                                newBlock.getVariables().set(index, newVar);
                            else {
                                newBlock.getVariables().add(newVar);
                            }
                        } catch (NumberFormatException e){ // Catches the exception thrown when trying to parse a String into an integer.
                            Variable newVar = new Variable(data.substring(lineStringIndexCounter + 4, lineStringIndexCounter + varLength + 4), 0);

                            // if a variable is already present in the block
                            boolean varAlreadyPresent = false;
                            int index = 0;
                            for (int i = 0; i < newBlock.getVariables().size(); i++){
                                if (newBlock.getVariables().get(i).getName().equals(newVar.getName())){
                                    index = i;
                                    varAlreadyPresent = true;
                                }
                            }
                            if (varAlreadyPresent)
                                newBlock.getVariables().set(index, newVar);
                            else {
                                newBlock.getVariables().add(newVar);
                                //localVar.add(newVar);
                            }
                        }
                        // Accounts for when there is no assignment operator present between the variable name and variable value after the int keyword
                        // has already been detected in the line.
                    } else {
                        Variable newVar = new Variable(data.substring(lineStringIndexCounter + 4, lineStringIndexCounter + varLength + 4), 0);

                        // if a variable is already present in the block
                        boolean varAlreadyPresent = false;
                        int index = 0;
                        for (int i = 0; i < newBlock.getVariables().size(); i++){
                            if (newBlock.getVariables().get(i).getName().equals(newVar.getName())){
                                index = i;
                                varAlreadyPresent = true;
                            }
                        }
                        if (varAlreadyPresent)
                            newBlock.getVariables().set(index, newVar);
                        else {
                            newBlock.getVariables().add(newVar);
                            //localVar.add(newVar);
                        }
                    }
                }
                lineStringIndexCounter++; // iteration of the while loop checking for the end of line keyword, ";"
            }
        }
    }

    // checks for int keyword

    /**
     * Checks the specific line that is passed in the parameter in that specific block for the int keyword and adds that variable to the variables linked list
     * in the passed in block from the program stack.
     * @param data
     *  The line that will be passed into the parser.
     * @param newBlock
     *  The block that the line currently being assessed is in.
     * @custom.precondition
     *  Data should be a String that contains a line in the C program passed in the file.
     *  NewBlock should be a block that is referring to the current block in the program stack.
     * @custom.postcondition
     *  Variables that are paired with the int keyword will be added to the variables linked list in the program stack block that is passed in.
     */
    public static void checkLineForIntKeyword(String data, Block newBlock){
        int lineStringIndexCounter = 0;

        // checks if the current line is a multi declaration line
        boolean multiDeclaration = false;
        while (!data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(";")) {
            if (data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(","))
                multiDeclaration = true;
            lineStringIndexCounter++;
        }
        lineStringIndexCounter = 0;

        // checks for the int keyword if the line is NOT a multi declaration line
        if (!multiDeclaration) {
            // while loop checks to see if we are at the end of the line accounting for only "int " (iterates through the line until it finds a semicolon)
            while (!(data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(";")) &&
                    !(data.substring(lineStringIndexCounter + 3, lineStringIndexCounter + 4).equals(";"))){
                // if found the int keyword then create an instance of the variables class and add it to the list in the block
                if (data.substring(lineStringIndexCounter, lineStringIndexCounter + 4).equals("int ")) { // lsic = 4
                    // finds how long the variable being declared is
                    int varLength = 0;
                    int tempLineStringIndexCounter = lineStringIndexCounter + 4;
                    while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(" ") &&
                            !data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(";")){
                        varLength++;
                        tempLineStringIndexCounter++;
                    }
                    // finds how long the integer value is
                    int intLength = 0;
                    tempLineStringIndexCounter = varLength + 7 + lineStringIndexCounter;
                    while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(" ") &&
                            !data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(";")) {
                        intLength++;
                        tempLineStringIndexCounter++;
                    }

                    // checks if there is an assignment operator or not
                    if (!data.substring(lineStringIndexCounter + varLength + 4, lineStringIndexCounter + varLength + 5).equals(";")) {
                        try {
                            Variable newVar = new Variable(data.substring(lineStringIndexCounter + 4, lineStringIndexCounter + varLength + 4),
                                    Integer.parseInt(data.substring(lineStringIndexCounter + varLength + 7, lineStringIndexCounter + varLength + 7 + intLength)));
                            // if a variable is already present in the block
                            boolean varAlreadyPresent = false;
                            int index = 0;
                            for (int i = 0; i < newBlock.getVariables().size(); i++){
                                if (newBlock.getVariables().get(i).getName().equals(newVar.getName())){
                                    index = i;
                                    varAlreadyPresent = true;
                                }
                            }
                            if (varAlreadyPresent)
                                newBlock.getVariables().set(index, newVar);
                            else
                                newBlock.getVariables().add(newVar);
                        } catch (NumberFormatException e){
                            Variable newVar = new Variable(data.substring(lineStringIndexCounter + 4, lineStringIndexCounter + varLength + 4), 0);

                            // if a variable is already present in the block
                            boolean varAlreadyPresent = false;
                            int index = 0;
                            for (int i = 0; i < newBlock.getVariables().size(); i++){
                                if (newBlock.getVariables().get(i).getName().equals(newVar.getName())){
                                    index = i;
                                    varAlreadyPresent = true;
                                }
                            }
                            if (varAlreadyPresent)
                                newBlock.getVariables().set(index, newVar);
                            else
                                newBlock.getVariables().add(newVar);
                        }
                    } else {
                        Variable newVar = new Variable(data.substring(lineStringIndexCounter + 4, lineStringIndexCounter + varLength + 4), 0);

                        // if a variable is already present in the block
                        boolean varAlreadyPresent = false;
                        int index = 0;
                        for (int i = 0; i < newBlock.getVariables().size(); i++){
                            if (newBlock.getVariables().get(i).getName().equals(newVar.getName())){
                                index = i;
                                varAlreadyPresent = true;
                            }
                        }
                        if (varAlreadyPresent)
                            newBlock.getVariables().set(index, newVar);
                        else {
                            newBlock.getVariables().add(newVar);
                            //localVar.add(newVar);
                        }
                    }
                }
                lineStringIndexCounter++; //
            }
        }

        // checks for the int keyword if the line is a multi declaration line
        if (multiDeclaration) {
            // count the amount of variables that are being multi declared on the single line.
            int numVarsInMultiDecLine = 0;
            while (!data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(";")) {
                if (data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(","))
                    numVarsInMultiDecLine++;
                lineStringIndexCounter++;
            }
            numVarsInMultiDecLine++; // accounts for the missing variable because we are counting the commas to get the amount of variables.
            lineStringIndexCounter = 0;

            // while loop checks to see if we are at the end of the line accounting for only "int " (iterates through the line until it finds a semicolon)
            while (!(data.substring(lineStringIndexCounter, lineStringIndexCounter + 1).equals(";")) &&
                    !(data.substring(lineStringIndexCounter + 3, lineStringIndexCounter + 4).equals(";"))) {
                // if found the int keyword then create an instance of the variables class and add it to the list in the block
                if (data.substring(lineStringIndexCounter, lineStringIndexCounter + 4).equals("int ")) { // 4
                    int tempLineStringIndexCounter = 0;
                    for (int i = 0; i < numVarsInMultiDecLine; i++) { // 3
                        while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(",") &&
                                !data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(";")) { // 13 15 23 9 22
                            tempLineStringIndexCounter++;
                        }
                        if (isInt(data.substring(tempLineStringIndexCounter - 1, tempLineStringIndexCounter))){
                            // finds how long the integer value is
                            int intLength = 0;
                            while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(" ")) {
                                intLength++;
                                tempLineStringIndexCounter--; // 11 13 21 19
                            }
                            intLength--; // compensate for the extra count for the comma
                            // finds how long the variable being declared is
                            int varLength = 0;
                            tempLineStringIndexCounter -= 3; // 8 10 18 16
                            while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(" ")) {
                                varLength++;
                                tempLineStringIndexCounter--; // 7
                            }

                            Variable newVar = new Variable(data.substring(tempLineStringIndexCounter + 1, tempLineStringIndexCounter + 1 + varLength),
                                    Integer.parseInt(data.substring(tempLineStringIndexCounter + 4 + varLength, tempLineStringIndexCounter + 4 + varLength + intLength)));
                                    // 13 - 1, 13

                            // if a variable is already present in the block
                            boolean varAlreadyPresent = false;
                            int index = 0;
                            for (int j = 0; j < newBlock.getVariables().size(); j++){
                                if (newBlock.getVariables().get(j).getName().equals(newVar.getName())){
                                    index = j;
                                    varAlreadyPresent = true;
                                }
                            }
                            if (varAlreadyPresent)
                                newBlock.getVariables().set(index, newVar);
                            else {
                                newBlock.getVariables().add(newVar);
                            }

                            tempLineStringIndexCounter += (5 + varLength + intLength);
                        } else{
                            int varLength = 0;

                            while (!data.substring(tempLineStringIndexCounter, tempLineStringIndexCounter + 1).equals(" ")) {
                                varLength++;
                                tempLineStringIndexCounter--; // 5 7 21 7
                            }
                            varLength--;

                            Variable newVar = new Variable(data.substring(tempLineStringIndexCounter + 1, tempLineStringIndexCounter + 1 + varLength), 0);
                            // if a variable is already present in the block
                            boolean varAlreadyPresent = false;
                            int index = 0;
                            for (int j = 0; j < newBlock.getVariables().size(); j++){
                                if (newBlock.getVariables().get(j).getName().equals(newVar.getName())){
                                    index = j;
                                    varAlreadyPresent = true;
                                }
                            }
                            if (varAlreadyPresent)
                                newBlock.getVariables().set(index, newVar);
                            else
                                newBlock.getVariables().add(newVar);

                            if (!data.substring(tempLineStringIndexCounter + 1 + varLength, tempLineStringIndexCounter + 2 + varLength).equals(";"))
                                tempLineStringIndexCounter += (2 + varLength);
                        }
                    }
                }
                lineStringIndexCounter++;
            }
        }
        //return localVar;
    }

    // checks if the String in the parameter is an integer
    /**
     * Checks if the String in the parameter is of integer type.
     * @param str
     *  The string that is being assessed on whether it is an integer or not.
     * @return
     *  Returns whether the string str is a number or not.
     * @custom.precondition
     *  Str must be of string type.
     * @custom.postcondition
     *  The result of whether str is an integer will be returned.
     */
    public static boolean isInt(String str){
        try{
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * Prints out the local variables when called.
     * @param newBlock
     *  The block that currently holds all the variables that are local.
     * @custom.precondition
     *  NewBlock must be referring to the block that wants the local variables to be printed out.
     * @custom.postcondition
     *  The local variables a part of the passed in block will be printed.
     */
    public static void printLocalKeyword(Block newBlock) {
        if (!newBlock.getVariables().isEmpty()) {
            System.out.printf("%-15s%-15s", "Variable Name", "Initial Value"); // Format printing.
            System.out.println();
            // Iterates through the entire variables linked list in the current block to access all the names and initial values that are local.
            for (int i = 0; i < newBlock.getVariables().size(); i++) {
                System.out.printf("%-15s%-15d", newBlock.getVariables().get(i).getName(), newBlock.getVariables().get(i).getInitialValue());
                System.out.println();
            }
        } else{
            // If the variables linked list is empty, then this print statement will be printed out to the console.
            System.out.println("No local variables to print.");
        }
        System.out.println();
    }

    // checks if the print a certain variable keyword is detected and prints the certain variable if it is
    /**
     * Checks if the print a certain variable keyword is detected and prints the certain variable if it is.
     * @param variable
     *  The variable that is to be printed if it exists in any of the blocks in the program stack.
     * @param programStack
     * The program stack is a stack that contains all the different blocks in the C code.
     * @custom.precondition
     *  Variable is of String type and is the variable that wants to be printed.
     *  ProgramStack is a stack of blocks that has all the blocks in order, with the most recent block addition at the top.
     */
    public static void printVariableKeyword(String variable, Stack<Block> programStack) {
        Stack<Block> tempStack = new Stack<>(); // Creates a temporary stack for the program stack to pop its values into.

        boolean hasVar = false; // condition
        while (!programStack.isEmpty()) { // Goes until the program stack is empty to retain structure.
            // Goes through each variables in the variables linked list.
            for (int i = 0; i < programStack.peek().getVariables().size(); i++) {
                // If the variable is found, then it will be printed, but the popping of the program stack will continue to retain structure of the stack.
                if (programStack.peek().getVariables().get(i).getName().equals(variable) && !hasVar) {
                    System.out.printf("%-15s%-15s", "Variable Name", "Initial Value");
                    System.out.println();
                    System.out.printf("%-15s%-15d", programStack.peek().getVariables().get(i).getName(), programStack.peek().getVariables().get(i).getInitialValue());
                    System.out.println();
                    hasVar = true;
                }
            }
            tempStack.push(programStack.pop()); // Pushes blocks that are popped off the program stack into the temporary stack.
        }
        if (!hasVar)
            System.out.println("Variable not found: " + variable);
        System.out.println();

        // Reverts the program stack to its previous state before the call to the method.
        while (!tempStack.isEmpty()){
            programStack.push(tempStack.pop());
        }
    }
}
