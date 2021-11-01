package seedu.duke.command;

import seedu.duke.command.exception.ShelfNotExistException;
import seedu.duke.model.Item;
import seedu.duke.model.Shelf;
import seedu.duke.command.exception.IllegalArgumentException;
import seedu.duke.command.exception.DuplicateItemException;
import seedu.duke.model.ShelfList;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * The command that adds a new item to the list.
 */
public class AddCommand extends Command {
    public static final String ADD_ITEM_DATA_ARGS_FORMAT_STRING =
        "add n/NAME shlv/SHELF_NAME p/PURCHASE_PRICE s/SELLING_PRICE q/QUANTITY [r/REMARKS]";
    public static final String ADD_STRING = "add";
    public static final String PARSE_ADD_SUCCESS_MESSAGE_FORMAT = "name: %s\nshelfname: %s\ncost: $%s\n"
        + "price: %s\nquantity: %s\nremarks: %s\n";
    private static final String ADD_COMPLETE_MESSAGE_SINGLE =
            "This item has been added to the list.";
    private static final String ADD_COMPLETE_MESSAGE_MULTIPLE =
            " items have been added to the list.";
    private static final String PRICE_WARNING =
            "\nYour selling price is not higher than your purchase cost. "
                    + "\nMake sure you did not type wrongly";
    private static final String ZERO_PRICE_WARNING =
            "Your selling price and/or your purchase cost is 0. "
                    + "\nMake sure you did not type wrongly";
    private final String name;
    private final String purchaseCost;
    private final String sellingPrice;
    private final int quantity;
    private final String shelfName;
    private final String remarks;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * AddCommand Constructor.
     *
     * @param name         the name of the new item
     * @param purchaseCost the cost of the item
     * @param sellingPrice the price of the item
     */
    public AddCommand(String name, String purchaseCost, String sellingPrice, String quantity,
                      String shelfName, String remarks) {
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.sellingPrice = sellingPrice;
        this.quantity = Integer.parseInt(quantity);
        this.shelfName = shelfName;
        this.remarks = remarks;
    }

    /**
     * Executes the operation of adding the item to the list.
     *
     * @throws IllegalArgumentException if the input argument is wrong
     * @throws DuplicateItemException   if exactly the same item is added to the list
     */
    @Override
    public String execute() throws IllegalArgumentException, DuplicateItemException,
            ShelfNotExistException {
        try {
            Shelf selectedShelf = ShelfList
                    .getShelfList()
                    .getShelf(shelfName);
            for (int i = 0; i < quantity; i++) {
                int sizeBeforeAdding = selectedShelf.getSize();
                Item newItem = new Item(name, purchaseCost, sellingPrice, remarks);
                selectedShelf.addItem(newItem);
                int sizeAfterAdding = selectedShelf.getSize();
                assert sizeBeforeAdding + 1 == sizeAfterAdding :
                        "After adding an item the list size should increase by 1";
                logger.log(Level.INFO, "AddCommand successfully executed.");
            }
            boolean hasNegativeProfit = (new BigDecimal(sellingPrice).compareTo(new BigDecimal(purchaseCost)) == -1);

            double cost = Double.parseDouble(purchaseCost.trim());
            double price = Double.parseDouble(sellingPrice.trim());
            if (cost == 0 || price == 0) {
                return ZERO_PRICE_WARNING;
            }

            if (quantity > 1) {
                if (hasNegativeProfit) {
                    return quantity + ADD_COMPLETE_MESSAGE_MULTIPLE + PRICE_WARNING;
                } else {
                    return quantity + ADD_COMPLETE_MESSAGE_MULTIPLE;
                }
            } else if (quantity == 1) {
                if (hasNegativeProfit) {
                    return ADD_COMPLETE_MESSAGE_SINGLE + PRICE_WARNING;
                } else {
                    return ADD_COMPLETE_MESSAGE_SINGLE;
                }
            } else {
                throw new IllegalArgumentException("Item's quantity cannot be 0");
            }

        } catch (seedu.duke.model.exception.IllegalArgumentException e) {
            logger.log(Level.WARNING, String.format("AddCommand failed to execute with error message %s",
                    e.getMessage()));
            throw new IllegalArgumentException(e.getMessage());
        } catch (seedu.duke.model.exception.DuplicateItemException e) {
            logger.log(Level.WARNING, String.format("AddCommand failed to execute with error message %s",
                    e.getMessage()));
            throw new DuplicateItemException(e.getMessage());
        } catch (seedu.duke.model.exception.ShelfNotExistException e) {
            throw new ShelfNotExistException(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand command = (AddCommand) other;
        return name.equals(command.name)
                && purchaseCost.equals(command.purchaseCost)
                && sellingPrice.equals(command.sellingPrice);
    }
}
