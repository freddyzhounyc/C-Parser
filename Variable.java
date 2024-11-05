/**
 * The Variable Class represents what a variable is defined to be in this case. A variable has a name and an initial value that will not be changed unless the int keyword
 * is called in the code.
 *
 * @author Freddy Zhou
 *      Email: freddy.zhou@stonybrook.edu
 *      Stony Brook ID: 116580337
 *      HW #3 - C Parser
 *      CSE 214
 *      Recitation Number: R04 | TA Names: Veronica Oreshko, Anuj Sureshbhai, Alex Zuzow
 */

public class Variable{
    private String name; // The name of the variable.
    private int initialValue; // The initial value that the variable holds.

    /**
     * Instantiates an instance of the Variable class without any parameters.
     * @custom.postcondition
     *  An instance of the variable class will be created with no values in the instance variables.
     */
    public Variable(){
    }

    /**
     * Instantiates an instance of the Variable class with two parameters: name and initialValue.
     * @param name
     *  The name of the variable.
     * @param initialValue
     *  The initial value of the variable.
     * @custom.precondition
     *  Name must be of String type.
     *  InitialValue must be of int type.
     * @custom.postcondition
     *  An instance of the variable class will have been created with values associated to both of the instance variables: name and initialValue.
     */
    public Variable(String name, int initialValue){
        this.name = name;
        this.initialValue = initialValue;
    }

    // Setters
    /**
     * Sets the name of the variable to the input from the parameter.
     * @param name
     *  The name of the variable.
     * @custom.precondition
     *  Name must be of String type.
     * @custom.postcondition
     *  The instance variable, name, will be updated.
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Sets the initial value of the variable to the input from the parameter.
     * @param initialValue
     *  The initial value of the variable.
     * @custom.precondition
     *  Initial value must be of int type.
     * @custom.postcondition
     *  The instance variable, initialValue, will be updated.
     */
    public void setInitialValue(int initialValue){
        this.initialValue = initialValue;
    }

    // Getters
    /**
     * Gets the name of the variable.
     * @return
     *  Returns the name of the variable.
     * @custom.precondition
     *  Name must have a value associated with it.
     * @custom.postcondition
     *  The name of the variable will be returned.
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the initial value of the variable.
     * @return
     *  Returns the initial value of the variable.
     * @custom.precondition
     *  InitialValue must have a value associated with it.
     * @custom.postcondition
     *  The initial value of the variable will be returned.
     */
    public int getInitialValue(){
        return initialValue;
    }

    /**
     * Determines whether the current variable object is equal to the parameter variable in terms of content.
     * @param obj
     *  The object, or variable in this instance, that is being compared to the current variable.
     * @return
     *  Returns whether the two variable objects are equal in terms of content.
     */
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Variable){
            Variable tester = (Variable)obj;
            boolean equalName = false, equalInitialValue = false;
            if (tester.name.equals(name))
                equalName = true;
            if (tester.initialValue == initialValue)
                equalInitialValue = true;
            if (equalName && equalInitialValue)
                return true;
        }
        return false;
    }
}