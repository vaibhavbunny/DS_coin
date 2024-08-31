package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  // Constructor to initialise the Transaction Block

  TransactionBlock(Transaction[] t) {

    // Done this so that there is no change reflected in trarray if t is changed

    trarray = new Transaction[t.length];
    for (int i = 0; i < t.length; i++) {
      trarray[i] = t[i];
    }

    previous = null;
    Tree = new MerkleTree();
    trsummary = Tree.Build(trarray);
    dgst = null;
  }

  // Checks if the transaction is valid or not by checking double spending in
  // between coinsrc block and current block and also in the current block

  public boolean checkTransaction(Transaction t) {

    // The miner reward case where the coinsourceblock is set null

    if (t.coinsrc_block == null) {
      return true;
    }

    // Checking the double spending in between the coinsrc block and current block

    TransactionBlock temp = this.previous;
    while (temp != t.coinsrc_block) {

      for (int i = 0; i < temp.trarray.length; i++) {
        if (temp.trarray[i].coinID.equals(t.coinID)) {
          return false;
        }
      }

      temp = temp.previous;
    }

    // Now to check the double spending in current block itself

    // Count variable is to check double spending in the current block itself

    int count = 0;

    for (int j = 0; j < t.coinsrc_block.trarray.length; j++) {
      if (t.coinsrc_block.trarray[j].coinID.equals(t.coinID)
          && t.coinsrc_block.trarray[j].Destination.UID.equals(t.Source.UID))
        count++;
    }
    if (count == 1) {
      return true;
    }

    return false;
  }
}
