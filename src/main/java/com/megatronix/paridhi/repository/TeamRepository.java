package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.model.Event;
import com.megatronix.paridhi.model.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository< Team, Long > {
	List< Team > findByEvent ( Event event );

	@EntityGraph ( attributePaths = {"contacts"} )
	@Query ( "SELECT DISTINCT t FROM Team t JOIN t.gidList gid WHERE gid IN :gidList" )
	List< Team > findByGidListWithContacts ( @Param ( "gidList" ) List< String > gidList );

	@Query( "SELECT DISTINCT t FROM Team t JOIN t.gidList gid WHERE gid IN :gidList" )
	List< Team> findByGidList ( List< String> gidList );

	@EntityGraph ( attributePaths = {"contacts"} )
	Optional< Team > findByTid ( String tid );

	@EntityGraph ( attributePaths = {"contacts"} )
	@Query ( "SELECT t FROM Team t WHERE t.event = :event" )
	List< Team > findByEventWithContacts ( @Param ( "event" ) Event event );

	List< Team > findByHasPaid ( boolean hasPaid );

	List< Team > findByHasPlayed ( boolean hasPlayed );

	boolean existsByTeamNameAndEvent ( String teamName, Event event );

	@Query ( "SELECT COUNT(t) > 0 FROM Team t WHERE :gid MEMBER OF t.gidList" )
	boolean existsByGid ( @Param ( "gid" ) String gid );
}