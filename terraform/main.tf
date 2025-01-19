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
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  availability_zone = "us-east-1a"

  tags = {
    Name = "Subnet Public"
  }
}
resource "aws_subnet" "aws_subnet_public_b" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.4.0/24"
  map_public_ip_on_launch = true
  availability_zone = "us-east-1b"

  tags = {
    Name = "Subnet Public"
  }
}

resource "aws_subnet" "aws_subnet_private_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "us-east-1a"

  tags = {
    Name = "Private Subnet A"
  }
}

resource "aws_subnet" "aws_subnet_private_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.3.0/24"
  availability_zone = "us-east-1b"

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

resource "aws_route_table" "public_a" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "Public Route Table"
  }
}
resource "aws_route_table" "public_b" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "Public Route Table"
  }
}

resource "aws_route_table_association" "public_a" {
  subnet_id      = aws_subnet.aws_subnet_public_a.id
  route_table_id = aws_route_table.public_a.id
}

resource "aws_route_table_association" "public_b" {
  subnet_id      = aws_subnet.aws_subnet_public_b.id
  route_table_id = aws_route_table.public_b.id
}

resource "aws_security_group" "web_sg" {
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # HTTP-Zugriff von �berall
  }

    ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # HTTP-Zugriff von �berall
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Web Server SG"
  }
}

resource "aws_instance" "web_server" {
  ami               = "ami-0866a3c8686eaeeba" 
  instance_type     = "t2.micro"
  subnet_id         = aws_subnet.aws_subnet_public_a.id
  vpc_security_group_ids = [aws_security_group.web_sg.id]

  depends_on = [aws_db_instance.db,aws_vpc.main,aws_subnet.aws_subnet_public_a]

user_data = <<-EOF
#!/bin/bash
sudo apt update
sudo update-java-alternatives --set java-1.17.0-openjdk-amd64 
git clone https://github.com/LasseRhys/CloudComputing.git
cd CloudComputing
sudo apt install maven -y
sudo apt install npm -y
sudo apt install openjdk-17-jdk -y
npm install
mvn clean install
echo "export USERNAME_DB=${var.username}" >> /etc/environment
echo "export PASSWORD_DB=${var.password}" >> /etc/environment
echo "export DATABASE=${aws_db_instance.db.address}" >> /etc/environment
mvn spring-boot:run


EOF 
}

resource "aws_security_group" "db_sg" {
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["10.0.1.0/24"] # Nur Zugriff vom �ffentlichen Subnetz
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

resource "aws_db_subnet_group" "db_subnet_group_private" {
  name       = "db_subnet_group_private"
  subnet_ids = [aws_subnet.aws_subnet_private_a.id, aws_subnet.aws_subnet_private_b.id]


}

resource "aws_db_subnet_group" "db_subnet_group_public" {
  name       = "db_subnet_group_public"
  subnet_ids = [aws_subnet.aws_subnet_public_a.id, aws_subnet.aws_subnet_public_b.id]


}

resource "aws_db_instance" "db" {
  allocated_storage    = 10
  engine               = "postgres"
  engine_version       = "16.3"
  instance_class       = "db.t3.micro"
  username             = var.username
  password             = var.password
  db_subnet_group_name = aws_db_subnet_group.db_subnet_group_private.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  #final_snapshot_identifier = false
  skip_final_snapshot = true
  tags = {
    Name = "MyDB"
  }
}

resource "aws_s3_bucket" "Not static files" {
  bucket = "not static bucket"

  tags = {
    Name = "not Static Files Bucket"
  }
}

resource "aws_s3_bucket" "static_files" {
  bucket = "websitebucket" 

  acl    = "public-read" 

  tags = {
    Name = "Static Files Bucket"
  }
}

#  BucketPolicy einstellen
resource "aws_s3_bucket_policy" "static_files_policy" {
  bucket = aws_s3_bucket.static_files.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.static_files.arn}/*"
      }
    ]
  })
}

# Deployment  index
resource "aws_s3_object" "index_vue" {
  bucket       = aws_s3_bucket.static_files.id
  key          = "index.vue" 
  source       = "${path.module}/CloudComputing/src/views/kunde/dashboard/index.vue" 
  content_type = "text/html" #
}


#Loadblanacer

resource "aws_lb" "loadbalancer" {

  depends_on = [aws_instance.web_server] 

  name               = "loadbalancerEvent"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.web_sg.id]
  subnets            = [aws_subnet.aws_subnet_public_a.id, aws_subnet.aws_subnet_public_b.id]

  enable_deletion_protection = false
  idle_timeout {
    minutes = 60
  }

 
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "fixed-response"
    fixed_response {
      status_code = 200
      content_type = "text/plain"
      message_body = "Mahlzeit!"
    }
  }
}

resource "aws_lb_target_group" "web" {
  name     = "web-target-group"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    protocol = "HTTP"
    path     = "/"
    interval = 30
    timeout  = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_listener_rule" "web_listener_rule" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 1
  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.web.arn
  }

  condition {
    field  = "path-pattern"
    values = ["/"]
  }
}

resource "aws_lb_target_group_attachment" "web_target_attachment" {
  for_each = toset([aws_instance.web_server.id])

  target_group_arn = aws_lb_target_group.web.arn
  target_id        = each.value
  port             = 80
}



# NAT Gateway


resource "aws_eip" "nat" {
  vpc = true
  tags = {
    Name = "NAT Gateway IP"
  }
}

# NAT Gateway
resource "aws_nat_gateway" "nat_gateway" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.aws_subnet_public_a.id  # �ffentliches Subnetz
  tags = {
    Name = "NAT Gateway"
  }
}

# Route Table f�r private 
resource "aws_route_table" "nat_gateway_private" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main.id
  }

  tags = {
    Name = "Private Route Table"
  }
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