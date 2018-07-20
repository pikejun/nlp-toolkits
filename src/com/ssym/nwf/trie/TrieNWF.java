package com.ssym.nwf.trie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.ansj.dic.LearnTool;
import org.ansj.domain.Nature;
import org.ansj.domain.NewWord;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.nlpcn.commons.lang.util.IOUtil;

public class TrieNWF {
	
   public static void find(LearnTool learnTool, String sentence) {
	   NlpAnalysis nlpAnalysis = new NlpAnalysis().setLearnTool(learnTool) ;
	   nlpAnalysis.parseStr(sentence);
   }
   
   
   
   
   public static void save(LearnTool learnTool, String dstPath) {
		List<Entry<String, Double>> topTree = learnTool.getTopTree(0);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Double> entry : topTree) {
			sb.append(entry.getKey() + "\t" + entry.getValue()+"\n");
		}
		
		IOUtil.Writer(dstPath, IOUtil.UTF8, sb.toString());
   }
   
   
   public static void load(String filePath) throws IOException {
	   LearnTool learnTool = new LearnTool();
	   HashMap<String, Double> loadMap = IOUtil.loadMap(filePath, IOUtil.UTF8, String.class, Double.class);
	   for (Entry<String, Double> entry : loadMap.entrySet()) {
		   learnTool.addTerm(new NewWord(entry.getKey(), Nature.NW, entry.getValue()));
		   learnTool.active(entry.getKey());
	   }
	   System.out.println(learnTool.getTopTree(10));
   }


	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// 构建一个新词学习的工具类。这个对象。保存了所有分词中出现的新词。出现次数越多。相对权重越大。
		LearnTool learnTool = new LearnTool();
		NlpAnalysis nlpAnalysis = new NlpAnalysis().setLearnTool(learnTool);
		// 进行词语分词。也就是nlp方式分词，这里可以分多篇文章
		nlpAnalysis.parseStr("说过，社交软件也是打着沟通的平台，让无数土城豪有了肉体与精神的寄托。");
		nlpAnalysis.parseStr("其实可以打着这个需求点去运作的互联网公司不应只是社交类软件与可穿戴设备，还有携程网，去哪儿网等等，订房订酒店多好的寓意");
		nlpAnalysis.parseStr("张艺谋的卡宴，马明哲的戏");
		nlpAnalysis.parseStr("脱氧核糖核苷酸的效果不是很少啊");

		// 取得学习到的topn新词,返回前10个。这里如果设置为0则返回全部
		System.out.println(learnTool.getTopTree(10));

		// 只取得词性为Nature.NR的新词
		System.out.println(learnTool.getTopTree(10, Nature.NR));

		/**
		 * 将训练结果序列写入到硬盘中
		 */
		List<Entry<String, Double>> topTree = learnTool.getTopTree(0);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Double> entry : topTree) {
			sb.append(entry.getKey() + "\t" + entry.getValue()+"\n");
		}
		IOUtil.Writer("learnTool.snap", IOUtil.UTF8, sb.toString());
		sb = null;

		/**
		 * reload训练结果
		 */
		learnTool = new LearnTool() ;
		HashMap<String, Double> loadMap = IOUtil.loadMap("learnTool.snap", IOUtil.UTF8, String.class, Double.class);
		for (Entry<String, Double> entry : loadMap.entrySet()) {
			learnTool.addTerm(new NewWord(entry.getKey(), Nature.NW, entry.getValue()));
			learnTool.active(entry.getKey());
		}
		System.out.println(learnTool.getTopTree(10));
		
//		new File("learnTool.snap").delete();
	}

}
