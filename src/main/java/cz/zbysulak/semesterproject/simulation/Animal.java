/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zbysulak.semesterproject.simulation;

/**
 * Animal is entity what can move and reproduce.
 *
 * @author Zbyšek
 */
public interface Animal {

    /**
     * @return true if animal can be reproduced this day (it can if ==0)
     */
    int isHeadache();

    /**
     * set headache to param b. animal with headache cant reproduce.
     *
     * @param b
     */
    void setHeadache(int b);

    /**
     *
     * @return for how many days this animal have food
     */
    int getFood();

    /**
     * food represents for how many days this animal has food.
     *
     * @param i
     */
    void setFood(int i);
}
