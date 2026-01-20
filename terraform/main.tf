resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "VPC"
  }
}

resource "aws_subnet" "aws_subnet_public_a" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(aws_vpc.main.cidr_block,4,10)
  map_public_ip_on_launch = true
  availability_zone = "us-east-1a"

  tags = {
    Name = "Subnet Public"
  }
}
resource "aws_subnet" "aws_subnet_public_b" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(aws_vpc.main.cidr_block,4,11)
  map_public_ip_on_launch = true
  availability_zone = "us-east-1b"

  tags = {
    Name = "Subnet Public"
  }
}

resource "aws_subnet" "aws_subnet_private_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block              = cidrsubnet(aws_vpc.main.cidr_block,4,12)
  availability_zone = "us-east-1a"
  map_public_ip_on_launch = false

  tags = {
    Name = "Private Subnet A"
  }
}

resource "aws_subnet" "aws_subnet_private_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block              = cidrsubnet(aws_vpc.main.cidr_block,4,13)
  availability_zone = "us-east-1b"
  map_public_ip_on_launch = false

  tags = {
    Name = "Private Subnet B"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "Internet Gateway"
  }
}

resource "aws_eip" "nat_gateway_eip" {
  depends_on = [aws_internet_gateway.igw]
  vpc= true
  tags = {
    Name = "nat_gateway"
  }
}

resource "aws_nat_gateway" "nat_gateway" {
  depends_on    = [aws_internet_gateway.igw]
  allocation_id = aws_eip.nat_gateway_eip.id
  subnet_id     = aws_subnet.aws_subnet_public_a.id
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "Public Route Table"
  }
}
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
      nat_gateway_id = aws_nat_gateway.nat_gateway.id
  }

  tags = {
    Name = "Private Route Table"
  }
}

resource "aws_route_table_association" "public_a" {
  subnet_id      = aws_subnet.aws_subnet_public_a.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public_b" {
  subnet_id      = aws_subnet.aws_subnet_public_b.id
  route_table_id = aws_route_table.public.id
}
# Route Table subnet priv to subnet priv
resource "aws_route_table_association" "private_a" {
  subnet_id      = aws_subnet.aws_subnet_private_a.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "private_b" {
  subnet_id      = aws_subnet.aws_subnet_private_b.id
  route_table_id = aws_route_table.private.id
}

resource "aws_db_subnet_group" "db_subnet_group_private" {
  name       = "db_subnet_group_private"
  subnet_ids = [aws_subnet.aws_subnet_private_a.id, aws_subnet.aws_subnet_private_b.id]


}

resource "aws_db_subnet_group" "db_subnet_group_public" {
  name       = "db_subnet_group_public"
  subnet_ids = [aws_subnet.aws_subnet_public_a.id, aws_subnet.aws_subnet_public_b.id]


}

resource "aws_security_group" "web_sg" {
  name   = "eventtom_ec2"
  vpc_id = aws_vpc.main.id
}

resource "aws_security_group_rule" "allow_ssh_traffic_to_ec2" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.web_sg.id
}

resource "aws_security_group_rule" "allow_all_outbound_traffic_from_ec2" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.web_sg.id
}

resource "aws_security_group_rule" "allow_request_traffic_ec2" {
  type                     = "ingress"
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.lb_sg.id
  security_group_id        = aws_security_group.web_sg.id
}

resource "aws_security_group" "sg_eventtom_rds" {
  name   = "eventtom-rds-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.web_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "lb_sg" {
  name        = "load_balancer_sg"
  description = "Security group for ALB allowing HTTP traffic"
  vpc_id      = aws_vpc.main.id #rsetze mit deiner VPC-ID

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]  # Erlaubt ausgehenden Verkehr in alle Richtungen
  }

  tags = {
    Name = "lb-security-group"
  }
}
data "aws_iam_instance_profile" "vocareum_lab_instance_profile" {
  name = "LabInstanceProfile"
}

resource "aws_instance" "web_server" {
  ami               = "ami-0ecb62995f68bb549"
  instance_type     = "t2.micro"
  subnet_id         = aws_subnet.aws_subnet_public_a.id
  vpc_security_group_ids = [aws_security_group.web_sg.id]
  associate_public_ip_address = true
  iam_instance_profile = data.aws_iam_instance_profile.vocareum_lab_instance_profile.name


  depends_on = [aws_db_instance.db,aws_vpc.main,aws_subnet.aws_subnet_public_a,aws_nat_gateway.nat_gateway, aws_internet_gateway.igw]

  user_data = <<-EOF
              #!/bin/bash
              mkdir work
              cd work
              sudo touch .env
              echo 'DATABASE_URL=jdbc:postgresql://${aws_db_instance.db.address}:5432/postgres' | sudo tee -a .env
              echo 'DATABASE_USERNAME=${aws_db_instance.db.username}' | sudo tee -a .env
              echo 'DATABASE_PASSWORD=${aws_secretsmanager_secret_version.secret_manager.secret_string}' | sudo tee -a .env
              source /etc/environment
              sudo apt update

              sudo apt install apt-transport-https ca-certificates curl software-properties-common
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
              echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
              sudo apt-get update
              sudo apt install -y docker-ce docker-ce-cli containerd.io

              docker run -d -p 8080:8080  -e DATABASE_URL=jdbc:postgresql://${aws_db_instance.db.address}:5432/postgres  -e DATABASE_USERNAME=${aws_db_instance.db.username} -e DATABASE_PASSWORD=${aws_secretsmanager_secret_version.secret_manager.secret_string} rhysling/guenther4587:latest

              EOF
}

resource "aws_security_group" "db_sg" {
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    security_groups = [aws_security_group.web_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "DB Security Group"
  }
}



resource "aws_secretsmanager_secret" "rds_password" {
  name                    = "EVENTTOM_RDS_PASSWORD"
  recovery_window_in_days = 0
}
resource "aws_secretsmanager_secret_version" "secret_manager" {
  secret_id = aws_secretsmanager_secret.rds_password.id

  secret_string = var.password

  depends_on = [aws_secretsmanager_secret.rds_password]
}

resource "aws_db_instance" "db" {
  allocated_storage    = 10
  engine               = "postgres"
  engine_version       = "18.1"
  instance_class       = "db.t3.micro"
  username             = var.username
  password             = aws_secretsmanager_secret_version.secret_manager.secret_string
  db_subnet_group_name = aws_db_subnet_group.db_subnet_group_private.name
  vpc_security_group_ids = [aws_security_group.sg_eventtom_rds.id]
  #final_snapshot_identifier = false
  skip_final_snapshot = true
  publicly_accessible = false

}



##

#Loadblanacer

resource "aws_lb" "loadbalancer" {


  name               = "loadbalancerEvent"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.lb_sg.id]
  subnets            = [aws_subnet.aws_subnet_public_a.id, aws_subnet.aws_subnet_public_b.id]
  depends_on = [aws_internet_gateway.igw]


 
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.loadbalancer.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.web.arn
  }
}

resource "aws_lb_target_group" "web" {
  name     = "web-target-group"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    protocol = "HTTP"
    path     = "/health"
    interval = 30
    timeout  = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }
}



resource "aws_lb_target_group_attachment" "web_target_attachment" {


  target_group_arn = aws_lb_target_group.web.arn
  target_id        = aws_instance.web_server.id
  port             = 8080
}




