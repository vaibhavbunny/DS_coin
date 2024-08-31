package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  // Checking the transaction block in malicious blockchain

  public static boolean checkTransactionBlock(TransactionBlock tB) {

    CRF obj = new CRF(64);

    // Checking the dgst condition

    if (!tB.dgst.substring(0, 4).equals("0000"))
      return false;

    // Dgst check with only one block in the blockchain

    if (tB.previous == null) {
      if (!tB.dgst.equals(obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)))
        return false;
    }

    // Dgst check with general case in blockchain

    if (tB.previous != null) {
      if (!tB.dgst.equals(obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)))
        return false;
    }

    // Check trsummary of the merkle tree corresponding to the transaction in
    // transaction block

    MerkleTree Tree = new MerkleTree();
    if (!tB.trsummary.equals(Tree.Build(tB.trarray)))
      return false;

    // Checking if any of the transactions is invalid in the transaction block

    for (int i = 0; i < tB.trarray.length; i++) {
      if (tB.checkTransaction(tB.trarray[i]) == false)
        return false;
    }

    return true;
  }

  // Longest valid chain to get the last block in malicious blockchain

  public TransactionBlock FindLongestValidChain() {

    TransactionBlock ret = lastBlocksList[0];
    int max = 0;
    for (int i = 0; i < lastBlocksList.length; i++) {
      TransactionBlock temp = lastBlocksList[i];
      int indmax = 0;
      while (temp != null) {
        if (checkTransactionBlock(temp) == true)
          indmax = indmax + 1;
        else
          indmax = 0;
        temp = temp.previous;
      }

      if (indmax > max) {
        max = indmax;
        int compl = 0;
        TransactionBlock flag = lastBlocksList[i];
        while (flag != null) {
          compl++;
          flag = flag.previous;
        }
        flag = lastBlocksList[i];
        for (int j = 0; j < compl - max; j++) {
          flag = flag.previous;
        }
        ret = flag;
      }
    }
    return ret;

  }

  // Insertion in malicious blockchain using longest valid chain

  public void InsertBlock_Malicious(TransactionBlock newBlock) {

    TransactionBlock lastBlock = this.FindLongestValidChain();

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
        newBlock.nonce = st;
        newBlock.previous = lastBlock;
        newBlock.dgst = temp;
        int x = 0;

        // Updating the lastblocks list

        // This case is for replacing the existing last block with the newblock inserted

        for (int j = 0; j < lastBlocksList.length; j++) {
          if (lastBlocksList[j] == lastBlock) {
            lastBlocksList[j] = newBlock;
            x = 1;
            break;
          }
        }

        // This case is for adding a new last Block

        if (x == 0) {
          int q;
          for (q = 0; q < lastBlocksList.length; q++) {
            if (lastBlocksList[q] == null)
              break;
          }
          lastBlocksList[q] = newBlock;
        }
        return;
      }
    }
  }
}
