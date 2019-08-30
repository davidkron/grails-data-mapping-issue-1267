package grails.data.mapping.issue

class Group {

    String name
    User supervisor

    static mapping = {
        table 'T_GROUP'
    }
}
