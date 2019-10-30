package com.foryou.component.bean

class TreeNode {
    Integer id
    String data
    TreeNode parentNode
    List<TreeNode> childrenList
    Integer hierarchy

    TreeNode(String dada, Integer id, Integer hierarchy) {
        this.data = dada
        this.id = id
        this.hierarchy = hierarchy
    }

    String getData() {
        return data
    }

    void setData(String data) {
        this.data = data
    }

    void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy
    }

    int getHierarchy() {
        return this.hierarchy
    }

    TreeNode getParentNode() {
        return parentNode
    }

    void setParentNode(TreeNode parentNode) {
        this.parentNode = parentNode
    }

    List<TreeNode> getChildrenList() {
        return childrenList
    }

    boolean isLeaf() {
        if (this.childrenList == null) {
            return true
        } else {
            return this.childrenList.isEmpty()
        }
    }

    protected boolean equals(TreeNode node) {
        return this.getData() == node.getData()
    }


    List<TreeNode> findTreeNodeByName(String name) {
        def list = new ArrayList<TreeNode>()
        findTreeNodeByName(name, list)
        return list
    }

    void findTreeNodeByName(String name, List list) {
        if (this.data == name) {
            list.add(this)
        }
        // 如果是叶子则返回
        if (this.isLeaf()) {
            return
        }
        this.getChildrenList().forEach { it ->
            it.findTreeNodeByName(name, list)
        }
    }

    /* 找到一颗树中某个节点 */

    public findTreeNodeById = { int id ->
        if (this.id == id)
            return this
        if (childrenList == null) {
            return null
        } else {
            int childNumber = childrenList.size()

            childrenList.each { node ->
                def result = node.findTreeNodeById(id)
            }
            for (int i = 0; i < childNumber; i++) {
                TreeNode child = childrenList.get(i)
                TreeNode resultNode = child.findTreeNodeById(id)
                if (resultNode != null) {
                    return resultNode
                }
            }
        }
    }

    /**
     * 返回此节点的孩子节点列表中首次出现的指定元素的索引，或如果不包含元素，则返回-1
     * @param node
     * @return
     */
    protected int indexOf(TreeNode node) {
        List<TreeNode> list = this.getChildrenList()
        int length = list.size()
        for (int i = 0; i < length; i++) {
            if (list.get(i) == node)
                return i
        }
        return -1
    }

    /***
     * 添加一个孩子节点
     */
    void addChild(TreeNode childNode) {
        initChildList()
        // 如果存在该子节点，退出
        if (this.indexOf(childNode) >= 0)
            return
        else {
            // 设置父亲节点
            childNode.setParentNode(this)
            this.getChildrenList().add(childNode)
        }
    }

    private void initChildList() {
        if (this.getChildrenList() == null) {
            this.childrenList = new ArrayList<TreeNode>()
        }
    }

    /**
     * 返回当前节点的所有父辈节点
     */
    List<TreeNode> getElders() {
        List<TreeNode> elders = new ArrayList<TreeNode>()
        TreeNode parentNode = this.parentNode
        if (parentNode == null) {
            return elders
        } else {
            // 倒序插入
            elders.add(0, parentNode)
            elders.addAll(0, parentNode.getElders())
            return elders
        }
    }

    /**
     * 当前的节点是不是传入参数节点的晚辈孩子节点
     * @param node
     * @return
     */
    boolean isChildrenOf(List<TreeNode> node) {
        def childrenLis = new ArrayList<TreeNode>()
        def bol = false
        if (node != null && !node.isEmpty()) {
            node.forEach {
                childrenLis.addAll(it.getJuniors())
            }
            childrenLis.each {
                if (data == it.data) {
                    bol = true
                }
            }
        }
        return bol
    }

    /**
     * 当前的节点是不是传入参数节点的祖先节点
     * @param node
     * @return
     */
    boolean isParentOf(List<TreeNode> node) {
        def parentList = new ArrayList<TreeNode>()
        def bol = false
        if (node != null && !node.isEmpty()) {
            node.forEach {
                parentList.addAll(it.getElders())
            }
            parentList.each {
                if (data == it.data) {
                    bol = true
                }
            }
        }
        return bol
    }

    /**
     * 返回当前节点的所有晚辈节点
     */
    List<TreeNode> getJuniors() {
        List<TreeNode> juniors = new ArrayList<TreeNode>()
        List<TreeNode> childList = this.getChildrenList()
        if (childList == null) {
            return juniors
        } else {
            int length = childList.size()
            for (int i = 0; i < length; i++) {
                TreeNode junior = childList.get(i)
                juniors.add(junior)
                juniors.addAll(junior.getJuniors())
            }
            return juniors
        }
    }

    /**
     * 层次遍历
     *
     * @param times
     */
    void traverse(int times) {
        print(this, times)
        // 如果是叶子则返回
        if (this.isLeaf())
            return
        int length = this.getChildrenList().size()
        times = times + 1
        for (int i = 0; i < length; i++) {
            this.getChildrenList().get(i).traverse(times)
        }
    }

    void traverse(List<TreeNode> list) {
        list.add(this)
        // 如果是叶子则返回
        if (this.isLeaf())
            return
        int length = this.getChildrenList().size()
        for (int i = 0; i < length; i++) {
            this.getChildrenList().get(i).traverse(list)
        }
    }

    List<TreeNode> getAllNode() {
        def list = new ArrayList<>()
        traverse(list)
        return list
    }

    private static void print(TreeNode node, int times) {
        for (int i = 0; i < times; i++) {
            println("    ")
        }
        println(node.getData())
    }


    static List<TreeNode> sort(TreeNode root, List<TreeNode> array) {
        if (array.size() == 0)
            return array
        for (int i = 0; i < array.size(); i++)
            for (int j = 0; j < array.size() - 1 - i; j++)
                if (!greater(root, array[j + 1], array[j])) {
                    def temp = array[j + 1]
                    array[j + 1] = array[j]
                    array[j] = temp
                }
        return array
    }


    private static boolean greater(TreeNode root, TreeNode a, TreeNode b) {
        return (a.isParentOf(root.findTreeNodeByName(b.data))) && ((b.isChildrenOf(root.findTreeNodeByName(a.data))))
    }


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        TreeNode treeNode = (TreeNode) o
        if (data != treeNode.data) return false
        return true
    }

    int hashCode() {
        return (data != null ? data.hashCode() : 0)
    }

    @Override
    String toString() {
        return "data = $data id = $id hierarchy = $hierarchy\n"
    }

}
