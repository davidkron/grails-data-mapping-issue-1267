package grails.data.mapping.issue

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
@Rollback
class DetachedCriteriaProblemSpec extends Specification {

    def setup() {
        User supervisor = createUser('supervisor@company.com')
        User user1 = createUser('user1@company.com')
        User user2 = createUser('user2@company.com')

        Group group1 = createGroup('Group 1', supervisor)
        Group group2 = createGroup('Group 2', supervisor)

        assignGroup(user1, group1)
        assignGroup(user1, group2)
    }

    void "get all users which belong to a group of specific supervisor (using where query)"() {
        given:
        String supervisorEmail = 'supervisor@company.com'
        def query = User.where {
            def u = User
            exists GroupAssignment.where {
                return user.id == u.id && group.supervisor.email == supervisorEmail
            }
        }

        when:
        def result = query.list()

        then:
        result.size() == 1
    }

    void "get all users which belong to a group of specific supervisor (using detached criteria)"() {
        given:
        String supervisorEmail = 'supervisor@company.com'

        def subCriteria = new DetachedCriteria(GroupAssignment).build {
            eqProperty('user.id', 'this.id')
            group {
                supervisor {
                    eq('email', supervisorEmail)
                }
            }
        }

        def criteria = new DetachedCriteria(User).build {
            setAlias('u')
            exists(subCriteria.id())
        }

        when:
        def result = criteria.list()

        then:
        result.size() == 1
    }

    private User createUser(String email) {
        User user = new User()
        user.email = email
        user.save(flush: true)
        return user
    }

    private Group createGroup(String name, User supervisor) {
        Group group = new Group()
        group.name = name
        group.supervisor = supervisor
        group.save(flush: true)
        return group
    }

    private void assignGroup(User user, Group group) {
        GroupAssignment groupAssignment = new GroupAssignment()
        groupAssignment.user = user
        groupAssignment.group = group
        groupAssignment.save(flush: true)
    }
}
