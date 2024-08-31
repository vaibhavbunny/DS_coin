package DSCoinPackage;

import HelperClasses.*;

public class Moderator {

  // Honest Initializing

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    int k = 100000;

    // Setting the source for the coins

    Members obj = new Members();
    obj.UID = "Moderator";

    // Initialising the coins and distributing in Round Robin fashion

    for (int i = 0; i < coinCount; i++) {

      Transaction temp = new Transaction();
      temp.coinID = Integer.toString(k);
      k++;
      temp.Source = obj;
      temp.Destination = DSObj.memberlist[i % DSObj.memberlist.length];
      temp.coinsrc_block = null;
      DSObj.pendingTransactions.AddTransactions(temp);
    }

    // k-1 because in the last iteration k was incremented

    DSObj.latestCoinID = Integer.toString(k - 1);

    int j = 0;

    // Forming TransactionBlocks

    while (true) {

      Transaction[] arr = new Transaction[DSObj.bChain.tr_count];

      for (int q = 0; q < DSObj.bChain.tr_count; q++) {
        try {
          arr[q] = DSObj.pendingTransactions.RemoveTransaction();
        } catch (EmptyQueueException e) {
          e.printStackTrace();
        }
      }

      TransactionBlock temp = new TransactionBlock(arr);

      // Adding the coins in mycoins of the destination of transaction

      for (int i = 0; i < arr.length; i++) {
        arr[i].Destination.mycoins.add(new Pair<String, TransactionBlock>(arr[i].coinID, temp));
      }
      j += DSObj.bChain.tr_count;

      DSObj.bChain.InsertBlock_Honest(temp);

      // Condition to check if all the coins have been distributed

      if (j == coinCount)
        break;

    }
  }

  // Honest Initializing

  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    int k = 100000;

    // Setting the source for the coins

    Members obj = new Members();
    obj.UID = "Moderator";

    // Initialising the coins and distributing in Round Robin fashion

    for (int i = 0; i < coinCount; i++) {

      Transaction temp = new Transaction();
      temp.coinID = Integer.toString(k);
      k++;
      temp.Source = obj;
      temp.Destination = DSObj.memberlist[i % DSObj.memberlist.length];
      temp.coinsrc_block = null;
      DSObj.pendingTransactions.AddTransactions(temp);
    }

    // k-1 because in the last iteration k was incremented

    DSObj.latestCoinID = Integer.toString(k - 1);

    int j = 0;

    // Forming TransactionBlocks

    while (true) {

      Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
      for (int q = 0; q < DSObj.bChain.tr_count; q++) {
        try {
          arr[q] = DSObj.pendingTransactions.RemoveTransaction();
        } catch (EmptyQueueException e) {
          e.printStackTrace();
        }
      }

      TransactionBlock temp = new TransactionBlock(arr);

      // Adding the coins in mycoins of the destination of transaction

      for (int i = 0; i < arr.length; i++) {
        arr[i].Destination.mycoins.add(new Pair<String, TransactionBlock>(arr[i].coinID, temp));
      }

      j += DSObj.bChain.tr_count;

      DSObj.bChain.InsertBlock_Malicious(temp);

      // Condition to check if all the coins have been distributed

      if (j == coinCount)
        break;

    }
  }

}
