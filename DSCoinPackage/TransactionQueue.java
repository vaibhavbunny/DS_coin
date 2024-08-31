package DSCoinPackage;

public class TransactionQueue {

  // Self implemented queue

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  // ENQUEUE OPERATION

  public void AddTransactions(Transaction transaction) {

    // Case for empty queue

    if (firstTransaction == null) {
      firstTransaction = new Transaction();
      firstTransaction = transaction;
      firstTransaction.next = null;
      firstTransaction.previous = null;
      lastTransaction = firstTransaction;
      numTransactions = 1;
      return;
    }

    // General case for insertion

    Transaction newTransaction = new Transaction();
    newTransaction = transaction;
    newTransaction.next = null;
    lastTransaction.next = newTransaction;
    newTransaction.previous = lastTransaction;
    lastTransaction = newTransaction;
    numTransactions++;
    return;

  }

  // DEQUEUE OPERATION

  public Transaction RemoveTransaction() throws EmptyQueueException {

    // Empty queue case

    if (numTransactions == 0) {
      throw new EmptyQueueException();
    }

    // Single element case to be handled separately

    if (numTransactions == 1) {
      Transaction temp = firstTransaction;
      firstTransaction = null;
      numTransactions--;
      return temp;
    }

    // General deletion case

    Transaction temp = firstTransaction;
    firstTransaction = firstTransaction.next;
    firstTransaction.previous = null;
    numTransactions--;
    return temp;
  }

  // LENGTH OF QUEUE

  public int size() {
    return numTransactions;
  }
}
