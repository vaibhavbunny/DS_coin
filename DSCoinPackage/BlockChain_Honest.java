package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  // Insertion in Honest blockChain

  public void InsertBlock_Honest(TransactionBlock newBlock) {

    CRF obj = new CRF(64);
    String st = "";

    // Simultaneous nonce and dgst calculation

    for (long i = 1000000001L; i <= 9999999999L; i++) {
      st = String.valueOf(i);
      String temp;

      // Empty BlockChain case

      if (lastBlock == null) {
        temp = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + st);
      }

      // General Case

      else {
        temp = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + st);
      }

      // Now check for the dgst for which the first four letters are 0000 and set all
      // the attributes of the blockchain

      if (temp.substring(0, 4).equals("0000")) {
        newBlock.previous = lastBlock;
        newBlock.dgst = temp;
        newBlock.nonce = st;
        tr_count = newBlock.trarray.length;
        lastBlock = newBlock;
        return;
      }
    }

  }
}
