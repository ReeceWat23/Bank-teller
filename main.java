public class main {


 /** Seeded Bugs:
            * 1. Allows negative deposit.
 * 2. Withdraw doesn't check for overdraft (partially removed check).
            * 3. Allows transfers to the same account (Bank class bug).
            * 4. Transfer ignores rounding -> floating-point drift (using `amount` directly).
            * 5. Transaction history missing one side of transfer details (missing other account number).
            * no feedback on a successful or not transaction
 */
}
