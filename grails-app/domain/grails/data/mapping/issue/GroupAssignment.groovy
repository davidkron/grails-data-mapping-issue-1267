package grails.data.mapping.issue

class GroupAssignment {

    User user
    Group group

    static mapping = {
        table 'T_GROUP_ASSIGNENT'
    }
}
