package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.TreeNode;

public class Members {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  // Initiating coin send in Honest BlockChain

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {

    Pair<String, TransactionBlock> pr = mycoins.remove(0);
    Transaction obj = new Transaction();
    obj.coinID = pr.get_first();
    obj.coinsrc_block = pr.get_second();
    obj.Source = this;

    // Finding the destination for the transaction using destUID

    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if (DSobj.memberlist[i].UID.equals(destUID)) {
        obj.Destination = DSobj.memberlist[i];
        break;
      }
    }

    // Adding in the local array of transactions

    for (int j = 0; j < 100; j++) {
      if (in_process_trans[j] == null) {
        in_process_trans[j] = obj;
        break;
      }
    }

    DSobj.pendingTransactions.AddTransactions(obj);

  }

  // Initiating coin send in Malicious BlockChain

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    Pair<String, TransactionBlock> pr = mycoins.remove(0);
    Transaction obj = new Transaction();
    obj.coinID = pr.get_first();
    obj.coinsrc_block = pr.get_second();
    obj.Source = this;

    // Finding the destination for the transaction using destUID

    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if (DSobj.memberlist[i].UID.equals(destUID)) {
        obj.Destination = DSobj.memberlist[i];
        break;
      }
    }

    // Adding in the local array of transactions

    for (int j = 0; j < 100; j++) {
      if (in_process_trans[j] == null) {
        in_process_trans[j] = obj;
        break;
      }
    }

    DSobj.pendingTransactions.AddTransactions(obj);

  }

  // Finalising coin send in honest blockchain

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend(Transaction tobj,
      DSCoin_Honest DSObj) throws MissingTransactionException {
    TransactionBlock temp = DSObj.bChain.lastBlock;
    int index = 0;// This is used for the the index of the transaction in the merkle tree so as to
                  // compute the sibling coupled path to root

    int k = 0;// Use of this is to get the no. of blocks present in blockchain after the block
              // containing
    // transaction

    while (true) {
      int flag = 0;
      for (int i = 0; i < temp.trarray.length; i++) {
        if (temp.trarray[i] == tobj) {
          flag = 1;
          index = i;
          for (int q = 0; q < tobj.Destination.mycoins.size(); q++) {

            // This "if" condition so that the coins remain sorted in mycoins of destination

            if (Integer.parseInt(tobj.Destination.mycoins.get(q).get_first()) > Integer.parseInt(tobj.coinID)) {
              tobj.Destination.mycoins.add(q, new Pair<String, TransactionBlock>(tobj.coinID, temp));
              break;
            }
          }
        }
      }

      if (flag == 1) {
        break;
      }

      temp = temp.previous;
      k++;

      // Case when transaction is not present in the blockchain

      if (temp == null)
        throw new MissingTransactionException();
    }

    // scptr-> Sibling coupled path to root

    List<Pair<String, String>> scptr = new ArrayList<Pair<String, String>>();

    // Computing sibling coupled path to root

    TreeNode tn = temp.Tree.rootnode;

    int n = temp.trarray.length - 1;
    int m = 0;
    while (tn.left.left != null) {
      if (index <= ((n + m) / 2)) {
        tn = tn.left;
        n = (n + m) / 2;
      } else {
        tn = tn.right;
        m = (n + m) / 2;

      }
    }

    Pair<String, String> pr = new Pair<String, String>(tn.left.val, tn.right.val);
    scptr.add(pr);

    while (tn.parent != null) {
      Pair<String, String> ab = new Pair<String, String>(tn.parent.left.val, tn.parent.right.val);
      scptr.add(ab);
      tn = tn.parent;
    }

    Pair<String, String> pq = new Pair<String, String>(tn.val, null);
    scptr.add(pq);

    // kplustwo-> The next list for containing pairs

    List<Pair<String, String>> kplustwo = new ArrayList<Pair<String, String>>();

    // Use of temporary-> Because we need to store pairs from first block to last in
    // kplustwo we will store from last to first in temporary and then put it in
    // reverse order from
    // temporary to kplustwo

    // All this was done because we have not taken a next pointer and using only a
    // next pointer

    List<Pair<String, String>> temporary = new ArrayList<Pair<String, String>>();

    Pair<String, String> xz = new Pair<String, String>(temp.previous.dgst, null);
    kplustwo.add(xz);

    TransactionBlock key = DSObj.bChain.lastBlock;

    for (int i = 0; i < k + 1; i++) {
      Pair<String, String> ad = new Pair<String, String>(key.dgst,
          key.previous.dgst + "#" + key.trsummary + "#" + key.nonce);
      temporary.add(ad);
      key = key.previous;
    }

    // Putting the pairs in reverse order from temporary to kplustwo

    for (int j = temporary.size() - 1; j >= 0; j--) {
      kplustwo.add(temporary.remove(j));
    }

    // Deleting the transaction from the local in process tranaction

    Transaction[] temparr = new Transaction[in_process_trans.length];
    int z = 0;

    for (int d = 0; d < in_process_trans.length; d++) {
      if (in_process_trans[d] == tobj) {
        in_process_trans[d] = null;
        continue;
      }

      temparr[z] = in_process_trans[d];
      z++;
    }
    in_process_trans = temparr;

    Pair<List<Pair<String, String>>, List<Pair<String, String>>> xy = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(
        scptr, kplustwo);

    return xy;

  }

  // To check the validity of transaction in pendingTransactions

  public boolean checkTrans(Transaction t, TransactionBlock bl) {

    // Miner reward case where the coinsource block of the transaction is null

    if (t.coinsrc_block == null) {
      return true;
    }

    TransactionBlock temp = bl;
    while (temp != null && temp != t.coinsrc_block) {
      for (int i = 0; i < temp.trarray.length; i++) {
        if (temp.trarray[i].coinID.equals(t.coinID)) {
          return false;
        }
      }
      temp = temp.previous;
    }

    // This means the transaction is not present in the blockchain

    if (temp == null) {
      return false;
    }

    // This check if it is spent only once in the current block

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

  // Honest Mining

  public void MineCoin(DSCoin_Honest DSObj) {

    Transaction[] arr = new Transaction[DSObj.bChain.tr_count];

    int j = 0;
    int i = 0;

    while (i < (DSObj.bChain.tr_count - 1)) {
      int flag = 0;
      try {
        Transaction temp = DSObj.pendingTransactions.RemoveTransaction();

        // Validity of transaction check

        if (checkTrans(temp, DSObj.bChain.lastBlock) == false) {
          continue;
        }

        // Double occurence check

        for (int k = 0; k < j; k++) {
          if (temp.coinID.equals(arr[k].coinID)) {
            flag = 1;
            break;
          }
        }

        // If no double occurence then simply add the transaction in the array created

        if (flag == 0) {
          arr[j] = temp;
          j++;
          i++;
        }
      } catch (EmptyQueueException e) {
        e.printStackTrace();
      }

    }

    // Reward transaction

    Transaction minerRewardTransaction = new Transaction();

    // Incrementing the latestCoinID

    int x = Integer.parseInt(DSObj.latestCoinID) + 1;

    DSObj.latestCoinID = Integer.toString(x);
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;

    // Adding the reward transaction at the end of the array

    arr[DSObj.bChain.tr_count - 1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(arr);

    DSObj.bChain.InsertBlock_Honest(tB);

    Pair<String, TransactionBlock> pr = new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB);

    mycoins.add(pr);

  }

  // Malicious Mining

  public void MineCoin(DSCoin_Malicious DSObj) {

    Transaction[] arr = new Transaction[DSObj.bChain.tr_count];

    int j = 0;
    for (int i = 0; i < (DSObj.bChain.tr_count - 1); i++) {
      int flag = 0;
      Transaction temp;
      try {
        temp = DSObj.pendingTransactions.RemoveTransaction();

        // Validity check for Malicious transaction

        if (checkTrans(temp, DSObj.bChain.FindLongestValidChain()) == false) {
          i--;
          continue;
        }

        // Double occurence check

        for (int k = 0; k < j; k++) {
          if (temp.coinID.equals(arr[k].coinID)) {
            i--;
            flag = 1;
            break;
          }
        }

        // If no double occurence then simply add the transaction in the array created

        if (flag == 0) {
          arr[j] = temp;
          j++;
        }
      } catch (EmptyQueueException e) {
        e.printStackTrace();
      }

    }

    // Reward transaction

    Transaction minerRewardTransaction = new Transaction();

    // Incrementing the latestCoinID

    int x = Integer.parseInt(DSObj.latestCoinID) + 1;
    DSObj.latestCoinID = Integer.toString(x);
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;

    // Adding the reward transaction at the end of the array

    arr[DSObj.bChain.tr_count - 1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(arr);

    DSObj.bChain.InsertBlock_Malicious(tB);

    Pair<String, TransactionBlock> pr = new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB);

    mycoins.add(pr);

  }
}
