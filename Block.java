/**
 * The Block Class represents the different blocks in the C code read by the BlockTracer Class and implements a linked list to represent the variables in each block.
 *
 * @author Freddy Zhou
 */
import java.util.LinkedList;

public class Block{
    private LinkedList<Variable> variables; // Linked list implementation to be able to store variables in each block.

    /**
     * Creates an instance of the Block class with no parameters. Instantiates the linked list that will hold the variables.
     * @custom.postcondition
     *  The linked list that will hold variables will have been instantiated.
     */
    public Block(){
        variables = new LinkedList<>();
    }

    // Setters
    /**
     * Sets the variables linked list.
     * @param variables
     *  Linked list that hold the variables.
     * @custom.precondition
     *  Variables will be instantiated to be a linked list.
     * @custom.postcondition
     *  The variables linked list will be properly updated.
     */
    public void setVariables(LinkedList<Variable> variables){
        this.variables = variables;
    }

    // Getters

    /**
     * Gets the variables linked list.
     * @return
     *  Returns the linked list that holds the variables.
     * @custom.precondition
     *  The variables linked list will have been instantiated.
     * @custom.postcondition
     *  The variables linked list will be returned.
     */
    public LinkedList<Variable> getVariables(){
        return variables;
    }
}
