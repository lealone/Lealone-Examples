const opsTables = { 
    data() {
        return {
            result: ""
        }
    },
    methods: {
        readTables() {
            axios.post(OpsCenter.DatabaseService + '/read_all_database_objects', { jsessionid: lealone.currentUser })
            .then(response=> {
                var t = response.data.tables;
                for (var i = 0; i < t.length; i++) {
                    addTable(t[i].name, t[i].columns, t[i].id);
                }
                var n = response.data.nodes;
                for (var i = 0; i < n.length; i++) {
                    setNode(n[i].id, n[i].level, n[i].type, n[i].icon, n[i].text, n[i].link);
                }
                writeTree();
                this.result = tree.join(" ");
                tree = [];
            })
        }
    },
    mounted() {
        lealone.set(this.gid, this);
        this.readTables();
    }
}

/*
 * Copyright 2004-2021 H2 Group. Multiple-Licensed under the MPL 2.0, and the EPL 1.0
 * (https://h2database.com/html/license.html). * Initial Developer: H2 Group
 */
var nodeList = new Array();
var icons = new Array();
var tables = new Array();
var tablesByName = new Object();
var tree = new Array();

function Table(name, columns, i) {
    this.name = name;
    this.columns = columns;
    this.id = i;
}

function addTable(name, columns, i) {
    var t = new Table(name, columns, i);
    tables[tables.length] = t;
    tablesByName[name] = t;
}

function ins(s, isTable) {
    if (parent.h2query) {
        if (parent.h2query.insertText) {
            parent.h2query.insertText(s, isTable);
        }
    }
}

function refreshQueryTables() {
    if (parent.h2query) {
        if (parent.h2query.refreshTables) {
            parent.h2query.refreshTables();
        }
    }
}

function goToTable(s) {
    var t = tablesByName[s];
    if (t) {
        hitOpen(t.id);
        return true;
    }
    return false;
}

function loadIcons() {
    icons[0] = new Image();
    icons[0].src = "/ops/img/tree_minus.gif";
    icons[1] = new Image();
    icons[1].src = "/ops/img/tree_plus.gif";
}

function Node(level, type, icon, text, link) {
    this.level = level;
    this.type = type;
    this.icon = icon;
    this.text = text;
    this.link = link;
}

function setNode(id, level, type, icon, text, link) {
    nodeList[id] = new Node(level, type, icon, text, link);
}

function writeDiv(i, level, dist) {
    if (dist > 0) {
        tree.push("<div id=\"div" + (i - 1) + "\" style=\"display: none;\">");
    } else {
        while (dist++ < 0) {
            tree.push("</div>");
        }
    }
}

function writeTree() {
    loadIcons();
    var last = nodeList[0];
    for (var i = 0; i < nodeList.length; i++) {
        var node = nodeList[i];
        writeDiv(i, node.level, node.level - last.level);
        last = node;
        var j = node.level;
        while (j-- > 0) {
            tree.push("<img src=\"/ops/img/tree_empty.gif\"/>");
        }
        if (node.type == 1) {
            if (i < nodeList.length - 1 && nodeList[i + 1].level > node.level) {
                tree.push("<img onclick=\"hit(" + i + ");\" id=\"join" + i + "\" src=\"/ops/img/tree_plus.gif\"/>");
            } else {
                tree.push("<img src=\"/ops/img/tree_empty.gif\"/>");
            }
        }
        tree.push("<img src=\"/ops/img/tree_" + node.icon + ".gif\"/>&nbsp;");
        if (node.link == null) {
            tree.push(node.text);
        } else {
            var isTable = node.icon == "table";
            tree.push("<a id='" + node.text + "' href='/' onclick=\"insertText('" + node.link + "', " + isTable +", event)\">" + node.text + "</a>");
        }
        tree.push("<br />");
    }
    writeDiv(0, 0, -last.type);
}

function hit(i) {
    var theDiv = document.getElementById("div" + i);
    var theJoin = document.getElementById("join" + i);
    if (theDiv.style.display == 'none') {
        theJoin.src = icons[0].src;
        theDiv.style.display = '';
    } else {
        theJoin.src = icons[1].src;
        theDiv.style.display = 'none';
    }
}

function hitOpen(i) {
    var theDiv = document.getElementById("div" + i);
    var theJoin = document.getElementById("join" + i);
    theJoin.src = icons[0].src;
    theDiv.style.display = '';
}
