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
    gateway_id = aws_nat_gateway.nat_gateway.id
  }

  tags = {
    Name = "Public Route Table"
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



resource "aws_security_group" "web_sg" {
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # HTTP-Zugriff von überall
  }

    ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
   # source_security_group_id = aws_security_group #TODO
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
  count=1

  depends_on = [aws_db_instance.db,aws_vpc.main,aws_subnet.aws_subnet_public_a]

  user_data = <<-EOF
              #!/bin/bash
              sudo apt update
              sudo apt upgrade -y
              sudo touch .env
              echo 'DATABASE_URL="postgresql://${aws_db_instance.eventtom_rds.username}:${aws_secretsmanager_secret_version.eventtom_secret_manager.secret_string}@${aws_db_instance.eventtom_rds.address}:5432/${aws_db_instance.eventtom_rds.db_name}"' | sudo tee -a .env
              echo 'DATABASE_USERNAME="${var.USERNAME}"' | sudo tee -a .env
              echo 'DATABASE_PASSWORD="${var.PASSWORD}"' | sudo tee -a .env
              source /etc/environment
              docker run  -p 8080:8080  --env-file .env guenther4587:local

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

resource "aws_db_subnet_group" "db_subnet_group_private" {
  name       = "db_subnet_group_private"
  subnet_ids = [aws_subnet.aws_subnet_private_a.id, aws_subnet.aws_subnet_private_b.id]


}

resource "aws_db_subnet_group" "db_subnet_group_public" {
  name       = "db_subnet_group_public"
  subnet_ids = [aws_subnet.aws_subnet_public_a.id, aws_subnet.aws_subnet_public_b.id]


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
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  #final_snapshot_identifier = false
  skip_final_snapshot = true
  publicly_accessible = false

}

resource "aws_s3_bucket" "Not_static_files" {
  bucket = "notstaticbucket334i"

  tags = {
    Name = "not Static Files Bucket"
  }
}

resource "aws_s3_bucket" "static_files" {
  bucket = "websitebucketeventom3341"

 # acl    = "public-read"

  tags = {
    Name = "Static Files Bucket"
  }
}

# Public Access Block (muss deaktiviert werden, um öffentliche Policy zu nutzen)
resource "aws_s3_bucket_public_access_block" "static_files_public_access" {
  bucket = aws_s3_bucket.static_files.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_ownership_controls" "static_files_ownership" {
  bucket = aws_s3_bucket.static_files.id

  rule {
    object_ownership = "BucketOwnerEnforced"
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
  depends_on = [aws_s3_bucket_ownership_controls.static_files_ownership]
}

# Deployment  index
resource "aws_s3_object" "vue_files" {
  for_each = fileset("${path.module}/../src/views/kunde", "**/*.vue")

  bucket       = aws_s3_bucket.static_files.id
  key          = each.value
  source       = "${path.module}/../src/views/kunde/${each.value}"
  content_type = "text/html"
}

##

#Loadblanacer

resource "aws_lb" "loadbalancer" {


  name               = "loadbalancerEvent"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.web_sg.id]
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




