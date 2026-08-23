erDiagram

    car_parts {
        uuid id PK
        varchar name
        decimal price
        timestamp created_at
        timestamp updated_at
        boolean removed
    }

    steering_wheels {
        uuid id PK,FK
    }
    
    wheels {
        uuid id PK,FK
    }
    
    interiors {
        uuid id PK,FK
    }
    
    transmissions {
        uuid id PK,FK
        varchar gear_box
        varchar drive
    }
    
    car_models {
        uuid id PK
        varchar name
        varchar brand_name
        decimal standard_price
        varchar body
        varchar fuel
        float engine_power
        float engine_volume
        uuid steering_wheel_id FK
        uuid wheels_id FK
        uuid interior_id FK
        uuid transmission_id FK
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    cars {
        uuid id PK
        uuid model_id FK
        varchar color
        uuid steering_wheel_id FK
        uuid wheels_id FK
        uuid interior_id FK
        uuid transmission_id FK
        varchar car_type
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    testable_cars {
        uuid id PK
        uuid car_id FK
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    clients {
        uuid id PK
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    managers {
        uuid id PK
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    system_admins {
        uuid id PK
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    warehouse_admins {
        uuid id PK
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    order_states {
        uuid id PK
        varchar state_type
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    orders {
        uuid id PK
        uuid state_id FK
        uuid client_id FK
        uuid manager_id FK
        uuid car_id FK
        varchar order_type
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    test_drive_requests {
        uuid id PK
        uuid client_id FK
        uuid car_id FK
        timestamp request_time
        varchar state
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    parts_models {
        uuid id PK
        uuid part_id FK
        uuid model_id FK
        timestamp created_at
        timestamp updated_at
        boolean removed
    }
    
    steering_wheels ||--|| car_parts : "id"
    wheels ||--|| car_parts : "id"
    interiors ||--|| car_parts : "id"
    transmissions ||--|| car_parts : "id"
    
    car_models }o--|| steering_wheels : "steering_wheel_id"
    car_models }o--|| wheels : "wheels_id"
    car_models }o--|| interiors : "interior_id"
    car_models }o--|| transmissions : "transmission_id"
    
    cars }o--|| car_models : "model_id"
    cars }o--|| steering_wheels : "steering_wheel_id"
    cars }o--|| wheels : "wheels_id"
    cars }o--|| interiors : "interior_id"
    cars }o--|| transmissions : "transmission_id"
    
    testable_cars ||--|| cars : "car_id"
    
    orders }o--|| order_states : "state_id"
    orders }o--|| clients : "client_id"
    orders }o--|| managers : "manager_id"
    orders }o--|| cars : "car_id"
    
    test_drive_requests }o--|| clients : "client_id"
    test_drive_requests }o--|| cars : "car_id"
    
    parts_models }o--|| car_parts : "part_id"
    parts_models }o--|| car_models : "model_id"