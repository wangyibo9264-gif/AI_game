create table users (
    id bigint not null auto_increment,
    display_name varchar(100),
    guest boolean not null default true,
    created_at timestamp not null,
    primary key (id)
);

create table cases (
    id bigint not null auto_increment,
    code varchar(80) not null,
    title varchar(255) not null,
    summary text,
    difficulty varchar(40),
    estimated_minutes int,
    status varchar(40) not null,
    primary key (id),
    constraint uk_cases_code unique (code)
);

create table case_rules (
    id bigint not null auto_increment,
    case_id bigint not null,
    rule_code varchar(80) not null,
    rule_text text not null,
    truth_meaning text,
    display_order int not null,
    primary key (id),
    constraint fk_case_rules_case foreign key (case_id) references cases (id)
);

create table case_locations (
    id bigint not null auto_increment,
    case_id bigint not null,
    code varchar(80) not null,
    name varchar(160) not null,
    description text,
    unlock_stage int not null,
    primary key (id),
    constraint uk_case_locations_case_code unique (case_id, code),
    constraint fk_case_locations_case foreign key (case_id) references cases (id)
);

create table case_npcs (
    id bigint not null auto_increment,
    case_id bigint not null,
    location_id bigint not null,
    code varchar(80) not null,
    name varchar(160) not null,
    role_name varchar(160),
    personality text,
    speaking_style text,
    unlock_stage int not null,
    primary key (id),
    constraint uk_case_npcs_case_code unique (case_id, code),
    constraint fk_case_npcs_case foreign key (case_id) references cases (id),
    constraint fk_case_npcs_location foreign key (location_id) references case_locations (id)
);

create table npc_knowledge (
    id bigint not null auto_increment,
    npc_id bigint not null,
    known_facts text,
    revealable_clue_codes text,
    hidden_facts text,
    forbidden_topics text,
    primary key (id),
    constraint fk_npc_knowledge_npc foreign key (npc_id) references case_npcs (id)
);

create table case_clues (
    id bigint not null auto_increment,
    case_id bigint not null,
    clue_code varchar(80) not null,
    title varchar(255) not null,
    content text not null,
    category varchar(40) not null,
    unlock_stage int not null,
    critical boolean not null default false,
    primary key (id),
    constraint uk_case_clues_case_clue unique (case_id, clue_code),
    constraint fk_case_clues_case foreign key (case_id) references cases (id)
);

create table game_sessions (
    id bigint not null auto_increment,
    user_id bigint not null,
    case_id bigint not null,
    current_stage int not null default 1,
    status varchar(40) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (id),
    constraint fk_game_sessions_user foreign key (user_id) references users (id),
    constraint fk_game_sessions_case foreign key (case_id) references cases (id)
);

create table location_visits (
    id bigint not null auto_increment,
    session_id bigint not null,
    location_id bigint not null,
    visited_at timestamp not null,
    primary key (id),
    constraint fk_location_visits_session foreign key (session_id) references game_sessions (id),
    constraint fk_location_visits_location foreign key (location_id) references case_locations (id)
);

create table dialogue_messages (
    id bigint not null auto_increment,
    session_id bigint not null,
    npc_id bigint,
    sender varchar(40) not null,
    content text not null,
    stage int not null,
    created_at timestamp not null,
    primary key (id),
    constraint fk_dialogue_messages_session foreign key (session_id) references game_sessions (id),
    constraint fk_dialogue_messages_npc foreign key (npc_id) references case_npcs (id)
);

create table collected_clues (
    id bigint not null auto_increment,
    session_id bigint not null,
    clue_id bigint not null,
    importance varchar(40) not null default 'NORMAL',
    status varchar(40) not null default 'UNRESOLVED',
    collected_at timestamp not null,
    primary key (id),
    constraint uk_collected_clues_session_clue unique (session_id, clue_id),
    constraint fk_collected_clues_session foreign key (session_id) references game_sessions (id),
    constraint fk_collected_clues_clue foreign key (clue_id) references case_clues (id)
);

create table truth_reports (
    id bigint not null auto_increment,
    session_id bigint not null,
    event_summary text,
    responsible_person varchar(160),
    key_evidence text,
    rule_explanation text,
    npc_lies text,
    conclusion text,
    created_at timestamp not null,
    primary key (id),
    constraint fk_truth_reports_session foreign key (session_id) references game_sessions (id)
);

create table report_scores (
    id bigint not null auto_increment,
    report_id bigint not null,
    truth_score int not null,
    clue_score int not null,
    rule_score int not null,
    ending varchar(120),
    summary text,
    missed_points text,
    primary key (id),
    constraint fk_report_scores_report foreign key (report_id) references truth_reports (id)
);