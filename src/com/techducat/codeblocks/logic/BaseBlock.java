/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import com.techducat.codeblocks.Block;
import com.techducat.codeblocks.ui.CustomMxCell;
import com.techducat.codeblocks.util.LexiconHelper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

/**
 *
 * @author odada
 */
public class BaseBlock implements Serializable, Block, Callable<Object>, Comparable<BaseBlock> {

    public static Stack<List<AnimationContext>> ANIMATION_QUEUE = new Stack<>();

    public static ExecutorService SERVICE = Executors.newFixedThreadPool(20);

    public static final HashMap<String, Class<? extends BaseBlock>> INSTANCE_MAP = new HashMap<>();

    private Future future;

    protected List<Object> inputList;

    private List<Object> retained;

    static {
        INSTANCE_MAP.put("Looper", Looper.class);
        INSTANCE_MAP.put("Equal", Decision.class);
        INSTANCE_MAP.put("Is Type", Decision.class);
        INSTANCE_MAP.put("Greater", Decision.class);
        INSTANCE_MAP.put("Less", Decision.class);
        INSTANCE_MAP.put("Anagram", Anagram.class);
        INSTANCE_MAP.put("DayCalc", DayCalc.class);
        INSTANCE_MAP.put("Char Counter", XterCounter.class);
        INSTANCE_MAP.put("Basket", Basket.class);
        INSTANCE_MAP.put("Combinator", Combinator.class);
        INSTANCE_MAP.put("Lexicon", Lexicon.class);
        INSTANCE_MAP.put("Permutator", Permutator.class);
        INSTANCE_MAP.put("Reverser", Reverser.class);
        INSTANCE_MAP.put("Organizer", Organizer.class);
        INSTANCE_MAP.put("Splitter", PhraseSplitter.class);
        INSTANCE_MAP.put("Word Counter", WordCounter.class);
        INSTANCE_MAP.put("Fibonacci", Fibonacci.class);
        INSTANCE_MAP.put("Sequence", Progression.class);
        INSTANCE_MAP.put("Series", Progression.class);
        INSTANCE_MAP.put("Quadratic Equation", QuadraticEquation.class);
        INSTANCE_MAP.put("Linear Equation", LinearEquation.class);
    }

    private static final Logger LOGGER = Logger.getLogger(BaseBlock.class.getName());

    private Long id;

    public static int delay = 2;

    protected Set<Object> inputs;

    private String name;

    private Block inputBlock;

    private Set<Block> outputBlocks;

    private String description;

    private byte[] iconImageBytes;

    private byte[] blockImageBytes;

    private State state;

    private Object value;

    public BaseBlock() {
        this.setName(this.getClass().getSimpleName());
        this.outputBlocks = new HashSet<>();
    }

    public BaseBlock(String name) {
        this.setName(name);
        this.outputBlocks = new HashSet<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setDelay(int delay) {
        BaseBlock.delay = delay;
    }

    @Override
    public int getDelay() {
        return BaseBlock.delay;
    }

    public synchronized void chain(Block block, Object arg) {
        try {
            LOGGER.log(Level.INFO, "{0} -O-O-O- {1} <= {2}", new Object[]{block.getName(), this.getName(), arg});
            LOGGER.log(Level.INFO, "State => {0}", block.getState());
            setState(block.getState());
            inputs = new HashSet<>();
            if (arg instanceof Collection) {
                inputs.addAll((Collection<? extends Object>) arg);
            } else {
                inputs.add(arg);
            }

            block.setInputs(inputs);

            Future[] fts = block.initiate();
            for (Future f : fts) {
                LOGGER.log(Level.INFO, "Here we go again?...{0}", f.get());
            }

        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(BaseBlock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void sendIn(Set<Object> inputs) {
        this.inputs = inputs;
    }

    public synchronized void addExecution(Object output) {
        if (this.findRoot().equals(this) || ANIMATION_QUEUE.isEmpty()) {
            ANIMATION_QUEUE.add(Collections.synchronizedList(new ArrayList<>()));
        }

        LOGGER.log(Level.INFO, "ANIMATION_QUEUE_THIS => {0}", this);
        AnimationContext executionState = new AnimationContext(3);
        executionState.put("block", this.getId() != null ? this : this.getInputBlock());
        if (this instanceof Looper) {
            Set<Object> in = new HashSet();
            in.addAll(((Looper) this).getRetained());
//            in.remove(output);
            executionState.put("in", in);
        } else {
            Set in = new HashSet<>();
            in.addAll(this.getInputs());
            executionState.put("in", in);
        }
        executionState.put("out", output);
        if (this instanceof Decision) {
            LOGGER.log(Level.INFO, "addExecution -- {0} result => {1}", new Object[]{inputs, Decision.flag});
            executionState.put("branch", Decision.flag ? "YES" : "NO");
        }

        if(!ANIMATION_QUEUE.isEmpty()) {
            List l = ANIMATION_QUEUE.peek();

            if (l != null) {
                if (!l.contains(executionState)) {
                    l.add(executionState);
                }
            }

            LOGGER.log(Level.INFO, "ANIMATION_QUEUE => {0}", l);
        }
        LOGGER.log(Level.INFO, "ANIMATION_QUEUE_SIZE => {0}", ANIMATION_QUEUE.size());
    }

    @Override
    public synchronized Object sendOut(Object output) {
        LOGGER.log(Level.INFO, "{0} output => {1}", new Object[]{getName(), output});
        this.addExecution(output);
        this.getOutputBlocks().stream().forEach((block) -> {
            block.setState(state);
            block.getState().setBlock(this);
            if (output instanceof Set) {
                block.setInputs(new HashSet<>());
                block.getInputs().addAll((Collection<? extends Object>) output);
            } else {
                block.getInputs().add(output);
            }
            this.chain(block, output);

            LOGGER.log(Level.INFO, "Incrementing => {0}", state.getCount());
            state.increment(this);
            LOGGER.log(Level.INFO, "Incrementing++ => {0}", state.getCount());

            LOGGER.log(Level.INFO, "{0} of {1} [Observing] {2}", new Object[]{this.getOutputBlocks().size(), this.getOutputBlocks(), this.getName()});
        });

        return output;
    }

    @Override
    public void addOutputBlock(Block outputBlock) {
        if (outputBlocks == null) {
            outputBlocks = new HashSet<>();
        }
        outputBlocks.add(outputBlock);
        if (outputBlock != null) {
            outputBlock.setInputBlock(this);
        }
    }

    @Override
    public Set<Block> getOutputBlocks() {
        return outputBlocks;
    }

    @Override
    public void setOutputBlocks(Set<Block> outputBlocks) {
        this.outputBlocks = outputBlocks;
    }

    @Override
    public Block getInputBlock() {
        return inputBlock;
    }

    @Override
    public void setInputBlock(Block inputBlock) {
        this.inputBlock = inputBlock;
    }

    @Override
    public Set<Object> getInputs() {
        return inputs;
    }

    @Override
    public void setInputs(Set<Object> inputs) {
        this.inputs = inputs;
        inputList = new ArrayList<>();
        inputList.addAll(inputs);
        retained = new ArrayList<>();
        retained.addAll(inputs);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public BaseBlock findRoot() {
        BaseBlock r = (BaseBlock) this.getInputBlock();
        BaseBlock root = (BaseBlock) this.getInputBlock();
        StringBuilder b = new StringBuilder();
        while (r != null) {
            b.append(" <- ");
            b.append(r.getName());
            r = (BaseBlock) r.getInputBlock();
            if (r != null && r.getInputBlock() == null) {
                root = r;
            }
        }
        LOGGER.log(Level.INFO, "findRoot() => {0}", root);

        return root != null ? root : this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public ImageIcon getIconImage() throws IOException {
        InputStream in = new ByteArrayInputStream(iconImageBytes);
        return new ImageIcon(ImageIO.read(in));
    }

    @Column(length = 15000)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    public byte[] getIconImageBytes() {
        return iconImageBytes;
    }

    public void setIconImageBytes(byte[] iconImageBytes) {
        this.iconImageBytes = iconImageBytes;
    }

    @Column(length = 15000)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    public byte[] getBlockImageBytes() {
        return blockImageBytes;
    }

    public void setBlockImageBytes(byte[] blockImageBytes) {
        this.blockImageBytes = blockImageBytes;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    public static BaseBlock createInstance(Class<? extends BaseBlock> clazz, Long id, Object value, Collection<Object> inputs, int delay) throws InstantiationException, IllegalAccessException {
        BaseBlock instance = clazz.newInstance();

        instance.setId(id);
        instance.setName(clazz.getSimpleName());
        instance.setValue(value);
        instance.setInputs(new HashSet<>());
        instance.getInputs().addAll(inputs);
        instance.setDelay(delay);

        return instance;
    }

    public static void createLink(BaseBlock source, BaseBlock target, CustomMxCell sourceCell, CustomMxCell targetCell) {
        source.addOutputBlock(target);
        target.setInputBlock(source);
        sourceCell.setBlock(source);
        targetCell.setBlock(target);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseBlock other = (BaseBlock) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public void initInput() {
        inputList = new ArrayList<>();
        inputList.addAll(inputs);
        retained = new ArrayList<>();
        retained.addAll(inputs);
    }

    @Override
    public void readInput() {
        URL inputFileUrl = Lexicon.class.getResource("/com/techducat/codeblocks/resources/temp_input.txt");
        LOGGER.log(Level.INFO, "InputPath => {0}", inputFileUrl.getPath());
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File(inputFileUrl.getPath())));
            String w = null;
            List<Object> tempInputs = new ArrayList<>();
            while (null != (w = reader.readLine())) {
                tempInputs.add(w);
            }
            this.setInputs(new HashSet<>());
            this.getInputs().addAll(tempInputs);

            inputList = new ArrayList<>();
            inputList.addAll(inputs);
            retained = new ArrayList<>();
            retained.addAll(inputs);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LexiconHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LexiconHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return name + "{" + "id=" + id + ", value=" + value + '}';
    }

    @Override
    public int compareTo(BaseBlock o) {
        if (o != null && this.getId() != null) {
            if (o.getClass() != getClass()) {
                return -1;
            }
            return (int) (this.getId() - o.getId());
        } else {
            if (this.getId() != null) {
                return this.getId().intValue();
            } else {
                return -1;
            }
        }
    }

    @Override
    public void setValue(Object value) {
        LOGGER.log(Level.INFO, "BaseBlock.value={0}", value);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Future[] initiate() throws InterruptedException, ExecutionException {
        future = SERVICE.submit(this);
        return new Future[]{future};
    }

    @Override
    public Future getFuture() {
        return future;
    }

    @Override
    public Object call() throws Exception {
        LOGGER.log(Level.INFO, "~~{0}~~ executing...", getClass().getSimpleName());
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[]{getName(), inputs});
        this.setInputs(inputs);
        return this.sendOut(inputs);
    }

    @Override
    public List<Object> getRetained() {
        return retained;
    }

    @Override
    public boolean isInDisplayed() {
        return false;
    }
}
