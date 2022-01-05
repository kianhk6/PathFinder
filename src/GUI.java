import javafx.scene.control.SelectionMode;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


//implements: are the tools were using to get input from the user from front end GUI
public class GUI extends JPanel implements MouseWheelListener, MouseListener, KeyListener, ActionListener, MouseMotionListener, Observer{

    private JFrame window;
    private PathFinder pathFinder;
    private DijkstraAlg dijkstraAlg;
    private BreadthFirst BFS;
    private PriorityQueue<Node> unvisited;
    private PriorityQueue<Node> visited;
    private PriorityQueue<Node> BFSvisited;
    private PriorityQueue<Node> BFSunvisited;
    private final static int gridDimention = 10;
    private Character keyRightNow;
    private Node GUIstartNode;
    private Node GUIendNode;
    int isStartOn = 0;
    int isEndOn = 0;
    private Node pathFinderStartNode;
    private Node pathFinderEndNode;
    private List<Node> path;
    private Set<Node> blocks;
    private PriorityQueue<Node> closedNodes;
    private PriorityQueue<Node> openNodes;

    private static final int INCREMENT_SIZE = 300;
    private JComboBox selectMode;






    public GUI() {
        unvisited = new PriorityQueue<>();
        visited = new PriorityQueue<>();
        BFSvisited = new PriorityQueue<>();
        BFSunvisited = new PriorityQueue<>();
        path = null;
        closedNodes = new PriorityQueue<>();
        openNodes = new PriorityQueue<>();
        blocks = new HashSet<>();
        pathFinderEndNode = null;
        pathFinderStartNode = null;
        addMouseListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        keyRightNow = (char) '0';
        GUIstartNode = null;
        GUIendNode = null;
        window = new JFrame();
        window.setContentPane(this);
        window.getContentPane().setPreferredSize(new Dimension(900,900));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //how it would close
        window.setTitle("A* algorithm");
        window.pack();
        window.setVisible(true);

        revalidate();
        repaint();

    }



    //
    {
        JFrame frame = new JFrame();
        frame.setTitle("Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Font size and style
        Font font = new Font("Verdana", Font.BOLD, 30);

        JButton clearObstacles, clearEverything, findPath;

        JLabel mode;



        class RoundedBorder implements Border {
            private final int radius;

            RoundedBorder(int radius) {
                this.radius = radius;
            }

            public Insets getBorderInsets(Component c) {
                return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
            }

            public boolean isBorderOpaque() {
                return true;
            }

            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            }
        }

        mode = new JLabel("mode: ");
        mode.setHorizontalAlignment(JLabel.CENTER);
        String[] modes = {"A star", "Dijkstra", "Breadth-First Search"};
        selectMode = new JComboBox<>();
        selectMode.addItem(modes[0]);
        selectMode.addItem(modes[1]);
        selectMode.addItem(modes[2]);
        selectMode.setSelectedIndex(0);


        clearObstacles = new JButton("Clear Obstacles");
        clearObstacles.setSize(new Dimension(10, 40));
        clearObstacles.setBorder(new RoundedBorder(10));
        clearObstacles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "you have pressed the Clear Obstacles button");
                //pathFinder = null;
                blocks = new HashSet<>();
                closedNodes = new PriorityQueue<>();
                openNodes = new PriorityQueue<>();
                visited = new PriorityQueue<>();
                unvisited = new PriorityQueue<>();
                path = null;
                repaint();

                JOptionPane.showMessageDialog(null, "Obstacles are cleared!");
            }
        });

        //
        clearEverything = new JButton("Clear Everything");
        clearEverything.setSize(new Dimension(10, 40));
        clearEverything.setBorder(new RoundedBorder(10));
        clearEverything.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "you have pressed the Clear Everything button");
                blocks = new HashSet<>();
                GUIendNode = null;
                GUIstartNode = null;
                pathFinderStartNode = null;
                pathFinderEndNode = null;
                isEndOn ++;
                isStartOn ++;
                path = null;
                closedNodes = new PriorityQueue<>();
                openNodes = new PriorityQueue<>();
                visited = new PriorityQueue<>();
                unvisited = new PriorityQueue<>();
                repaint();

                JOptionPane.showMessageDialog(null, "Everything is cleared!");
            }
        });

        findPath = new JButton("Run");
        findPath.setSize(new Dimension(10, 40));
        findPath.setBorder(new RoundedBorder(10));
        findPath.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String selection = (String) selectMode.getSelectedItem();
                if (selection.equals("A star")) {
                    if (pathFinderEndNode != null && pathFinderStartNode != null) {
                        pathFinder = new PathFinder(pathFinderStartNode, pathFinderEndNode, 900 / gridDimention, 900 / gridDimention);
                        pathFinder.addObserver(GUI.this);
                        for (Node block : blocks) {
                            Node pathfindingBlock = new Node(block.getX() / gridDimention == 90 ? 89 : block.getX() / gridDimention, block.getY() / gridDimention == 90 ? 89 : block.getY() / gridDimention
                            );
                            pathFinder.getGrid().flipNode(pathfindingBlock.getX(), pathfindingBlock.getY());
                        }
                        path = pathFinder.findPath(pathFinder.aStar());
                        if (!pathFinder.hasSolution) {
                            JOptionPane.showMessageDialog(null, "there is no path to the end point");
                        }
                        repaint();
                    }
                } else if (selection.equals("Dijkstra")) {
                    if (pathFinderEndNode != null && pathFinderStartNode != null) {
                        dijkstraAlg = new DijkstraAlg(pathFinderStartNode, pathFinderEndNode, 900 / gridDimention, 900 / gridDimention);
                        dijkstraAlg.addObserver(GUI.this);
                        for (Node block : blocks) {
                            Node dijBlock = new Node(block.getX() / gridDimention == 90 ? 89 : block.getX() / gridDimention, block.getY() / gridDimention == 90 ? 89 : block.getY() / gridDimention
                            );
                            dijkstraAlg.getGrid().flipNode(dijBlock.getX(), dijBlock.getY());
                        }
                        path = dijkstraAlg.findPath(dijkstraAlg.findEndNode());

                        repaint();
                    }

                }
                else if(selection.equals("Breadth-First Search")){
                    if (pathFinderEndNode != null && pathFinderStartNode != null) {
                        BFS = new BreadthFirst(pathFinderStartNode, pathFinderEndNode, 900 / gridDimention, 900 / gridDimention);
                        BFS.addObserver(GUI.this);
                        for (Node block : blocks) {
                            Node BFSblock = new Node(block.getX() / gridDimention == 90 ? 89 : block.getX() / gridDimention, block.getY() / gridDimention == 90 ? 89 : block.getY() / gridDimention
                            );
                            BFS.getGrid().flipNode(BFSblock.getX(), BFSblock.getY());
                        }
                        path = BFS.findPath(BFS.BFS());



                        repaint();
                    }

                }
            }
        });


        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(5,1));
        p2.add(clearObstacles);
        p2.add(clearEverything);
        p2.add(findPath);
        p2.add(mode);
        p2.add(selectMode);

        frame.setLayout(new BorderLayout());
        frame.add(p2,BorderLayout.LINE_END);
        frame.pack();
        frame.setVisible(true);
    }




    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.MAGENTA);
        for (Node node : closedNodes) {
            g.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
        }

        g.setColor(Color.green);
        for (Node node : openNodes) {
            g.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
        }

        g.setColor(Color.MAGENTA);
        for (Node node : unvisited) {
            g.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
        }

        g.setColor(Color.green);
        for (Node node : visited) {
            g.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
        }
        g.setColor(Color.MAGENTA);
        for (Node node : BFSunvisited) {
            g.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
        }

        g.setColor(Color.green);
        for (Node node : BFSvisited) {
            g.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
        }



        g.setColor(Color.lightGray);
        for (int y = 0; y < this.getHeight(); y += gridDimention) { //cp
            for (int x = 0; x < this.getWidth(); x += gridDimention) {
                g.drawRect(y, x , gridDimention, gridDimention);
            }
        }

        if (GUIendNode != null) {
            g.setColor(Color.red);
            g.fillRect(GUIendNode.getX() + 1, GUIendNode.getY() + 1, gridDimention - 1, gridDimention - 1);
        }

        if (GUIstartNode != null) {
            g.setColor(Color.blue);
            g.fillRect(GUIstartNode.getX() + 1, GUIstartNode.getY() + 1, gridDimention - 1, gridDimention - 1);
        }


        if (path != null) {
            g.setColor(Color.BLUE);
            path.remove(pathFinderEndNode);
            path.remove(pathFinderStartNode);
            for (Node node : path) {
                g.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
            }
        }

        if (blocks != null) {
            g.setColor(Color.black);
            for (Node block : blocks) {
                g.fillRect(block.getX() + 1, block.getY() + 1, gridDimention - 1, gridDimention - 1);
            }
        }



    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String selection = (String) selectMode.getSelectedItem();
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (keyRightNow == 'e' || keyRightNow == 'E') {
                    isEndOn++;
                    int xSub = e.getX() % gridDimention;
                    int ySub = e.getY() % gridDimention;
                    if (isEndOn % 2 == 1 && GUIendNode == null) {
                        GUIendNode = new Node(e.getX() - xSub, e.getY() - ySub);
                        pathFinderEndNode = new Node(GUIendNode.getX() / gridDimention, GUIendNode.getY() / gridDimention);
                    } else {
                        GUIendNode = null;
                    }
                    repaint();
                } else if (keyRightNow == 's' || keyRightNow == 'S') {
                    int xSub = e.getX() % gridDimention;
                    int ySub = e.getY() % gridDimention;
                    isStartOn++;
                    if (isStartOn % 2 == 1 && GUIstartNode == null) {
                        GUIstartNode = new Node(e.getX() - xSub, e.getY() - ySub);
                        pathFinderStartNode = new Node(GUIstartNode.getX() / gridDimention, GUIstartNode.getY() / gridDimention);
                    } else {
                        GUIstartNode = null;
                    }
                    repaint();
                }
            }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (keyRightNow == 'z') {

            if (e.getWheelRotation() < 0) {


                repaint();
            } else if (e.getWheelRotation() > 0) {
                //zoom out


                repaint();

            }


        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();
        keyRightNow = key;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int xSub = e.getX() % gridDimention;
        int ySub = e.getY() % gridDimention;

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (keyRightNow == 'b') {
                Node block = new Node(e.getX() - xSub, e.getY() - ySub);
                blocks.add(block);
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void update(Observable o, Object arg) {

        String selection = (String) selectMode.getSelectedItem();

        if (selection.equals("A star")) {
            String operation = (String) arg;
            PathFinder p = (PathFinder) o;
            if (operation.equals(p.CLOSED)) {
                PriorityQueue<Node> closedList = p.getClosed();
                closedNodes = closedList;

                closedNodes.remove(pathFinderStartNode);
                Graphics x = getGraphics();

                x.setColor(Color.MAGENTA);
                for (Node node : closedNodes) {
                    x.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (operation.equals(PathFinder.OPEN)) {
                PriorityQueue<Node> openList = p.getOpenList();
                openNodes = openList;
                openNodes.remove(pathFinderStartNode);
                Graphics x = getGraphics();
                x.setColor(Color.green);
                for (Node node : openNodes) {
                    x.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);


                }
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if(selection.equals("Dijkstra")){
            String operation = (String) arg;
            DijkstraAlg d = (DijkstraAlg) o;
            if (operation.equals(d.UNVISITED)) {
                PriorityQueue<Node> unvisited = d.getUnsettled();
                this.unvisited = unvisited;
                this.unvisited.remove(pathFinderStartNode);
                Graphics x = getGraphics();
                x.setColor(Color.MAGENTA);
                for (Node node : this.unvisited) {
                    x.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (operation.equals(d.VISITED)) {
                PriorityQueue<Node> visit = d.getSettled();
                visited = visit;
                visited.remove(pathFinderStartNode);
                Graphics x = getGraphics();
                x.setColor(Color.green);
                for (Node node : visited) {
                    x.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);

                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(selection.equals("Breadth-First Search")){
            String operation = (String) arg;
            BreadthFirst d = (BreadthFirst) o;
            if(operation.equals("v")){
                PriorityQueue<Node> visit = d.getBFSvisited();
                visit.remove(pathFinderStartNode);
                this.BFSvisited = visit;
                Graphics x = getGraphics();
                x.setColor(Color.green);
                for (Node node : visit) {
                    x.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            else if(operation.equals("uv")){
                PriorityQueue<Node> unvisit = d.getBFSunvisited();
                this.BFSunvisited = d.getBFSunvisited();
                unvisit.remove(pathFinderStartNode);
                Graphics x = getGraphics();
                x.setColor(Color.yellow);
                for (Node node : unvisit) {
                    x.fillRect(node.getX() * gridDimention + 1, node.getY() * gridDimention + 1, gridDimention - 1, gridDimention - 1);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }

    }
}
