// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ads.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.ads.domain.Revision;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Revision_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Revision.entityManager;
    
    public static final EntityManager Revision.entityManager() {
        EntityManager em = new Revision().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Revision.countRevisions() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Revision o", Long.class).getSingleResult();
    }
    
    public static List<Revision> Revision.findAllRevisions() {
        return entityManager().createQuery("SELECT o FROM Revision o", Revision.class).getResultList();
    }
    
    public static Revision Revision.findRevision(long idRevision) {
        return entityManager().find(Revision.class, idRevision);
    }
    
    public static List<Revision> Revision.findRevisionEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Revision o", Revision.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Revision.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Revision.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Revision attached = Revision.findRevision(this.idRevision);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Revision.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Revision.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Revision Revision.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Revision merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
